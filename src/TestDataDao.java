import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public class TestDataDao {
	private Connection conn;
	private Jedis jedis;
	public TestDataDao() {
		
	}
	public TestDataDao(Jedis jedis) {
		this.jedis=jedis;
	}
	public void setConnAndJedis(Connection conn,Jedis jedis) {
		this.conn=conn;
		this.jedis=jedis;
	}
	public void setConnection(Connection conn){
		this.conn=conn;
	}
	public Map<Integer, List<String>> getTestData(int problem_id,boolean disk) throws SQLException{
		Map<Integer, List<String>> data=new HashMap<>();
		String sql="select id,in_put,out_put from teach_test_data where problem_id=?";
		PreparedStatement pstmt=conn.prepareStatement(sql);
		pstmt.setInt(1, problem_id);
		ResultSet res=pstmt.executeQuery();
		while(res.next()){
			List<String> io=new ArrayList<>();
			String in=res.getString("in_put");
			String out=res.getString("out_put");
			int id=res.getInt("id");
			io.add(in);io.add(out);
			data.put(id, io);
		}
		pstmt.close();
		res.close();
		return data;
	}
	//private List<S>
	public Map<Integer, List<String>> getTestData(int problem_id) throws SQLException{
		Map<Integer, List<String>> data=new HashMap<>();
		if(jedis.exists("pid_data:"+problem_id)){
			//System.out.println("¶Á»º´æ");
			List<String> pidData=jedis.lrange("pid_data:"+problem_id, 0, -1);
			for(String id:pidData){
				List<String> io=new ArrayList<>();
				Map<String, String> map=jedis.hgetAll("data_id:"+id);
				io.add(map.get("in:"));io.add(map.get("out:"));
				data.put(Integer.valueOf(id), io);
			}
		}else{
			String sql="select id,in_put,out_put from teach_test_data where problem_id=?";
			PreparedStatement pstmt=conn.prepareStatement(sql);
			pstmt.setInt(1, problem_id);
			ResultSet res=pstmt.executeQuery();
			Pipeline pipe=jedis.pipelined();
			while(res.next()){
				List<String> io=new ArrayList<>();
				Map<String, String> map=new HashMap<>();
				String in=res.getString("in_put");
				String out=res.getString("out_put");
				int id=res.getInt("id");
				io.add(in);io.add(out);
				data.put(id, io);
				map.put("in:", in);map.put("out:", out);
				//System.out.println("Ð´»º´æ");
				pipe.lpush("pid_data:"+problem_id,String.valueOf(id));
				pipe.hmset("data_id:"+id, map);
			}
			pipe.sync();
			pstmt.close();
			res.close();
		}
		return data;
	}
}
