import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Jedis;
public class Main {
	public static String path=null;
	public static void main(String[] args) throws InterruptedException{
		Jedis jedis=null;
		JedisPool pool=JedisPoolUtils.getPool();
		try{
			path=new File("").getCanonicalPath()+"/code/";
			System.out.println(path);
		}catch(IOException e){
		}	
		ExecutorService service=Executors.newCachedThreadPool();		
		long size=0;
		Map<String, String> map=null;
		ExecutorThread thread=new ExecutorThread(path, service);
		while(true){
			Thread.sleep(500);
			jedis=pool.getResource();
			size=jedis.llen("sub_id:");
			if(size>0){
				for(int i=0;i<size;i++){
					String sub_id=jedis.lindex("sub_id:", 0);
					map=jedis.hgetAll("sub_id:"+sub_id);
					try {
						thread=new ExecutorThread(path, service);
						thread.setInfo(sub_id, map);
						service.execute(thread);
						if(jedis.del("sub_id:"+sub_id)==1)
							jedis.lpop("sub_id:");
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
			}
			JedisPoolUtils.returnResource(pool, jedis);
		}
	}

}
