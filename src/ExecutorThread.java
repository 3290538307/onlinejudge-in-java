import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import redis.clients.jedis.Jedis;

public class ExecutorThread implements Runnable{
	private String subId,path;
	private Map<String, String> map;
	private static final String[] ends= {".cpp",".c",".java",".py"};
	private ExecutorService service;
	public ExecutorThread(String path,ExecutorService service) {
		this.path=path;
		this.service=service;
	}
	public void setInfo(String subId,Map<String, String> map){
		this.subId=subId;
		this.map=map;
	}
	@Override
	public void run() {
		String problem_id=map.get("problem_id:");
		String code=map.get("submit_code:");
		int submit_language=Integer.parseInt(map.get("submit_language:"));
		TtlListener timeListener=new TtlListener();
		MemoryListener memoryListener=new MemoryListener();
		SubmitCodeDao submitCodeDao=new SubmitCodeDao();
		TestDataDao dao=new TestDataDao();
		String file=null;
		
		if(submit_language==3) {
			code=code.replace("Temp", "Temp"+subId);
			System.out.println(code);
			file="Temp"+subId+".java";
		}else {
			file=subId+ends[submit_language-1];
		}
		OJ oj=new CommonOJ(path);
		oj.convertCodeToFile(code, path+file);
		Connection conn=ConnectionFactory.getConnection();
		Jedis jedis=JedisPoolUtils.getPool().getResource();
		submitCodeDao.setConnection(conn);
		dao.setConnAndJedis(conn, jedis);
		Map<Integer, List<String>>  data=null;
		try {
			data=dao.getTestData(Integer.parseInt(problem_id));
		} catch (NumberFormatException | SQLException e) {
			e.printStackTrace();
		}
		String testStates=oj.executeCode(code, data, service, timeListener, memoryListener,submit_language,subId);
		try {
			submitCodeDao.update(Integer.valueOf(subId), oj.getResult(), oj.getErrors(), testStates, (int)oj.getDeltaTime(), oj.getAccuracy(), oj.getRunMemory());
		} catch (NumberFormatException | SQLException e) {
			e.printStackTrace();
		}
		JedisPoolUtils.returnResource(JedisPoolUtils.getPool(), jedis);
		File file2=new File(path+file);
		file2.delete();
		if(submit_language==3){
			file2=new File(path+"Temp"+subId+".class");
		}else{
			file2=new File(path+subId);
		}
		file2.delete();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
