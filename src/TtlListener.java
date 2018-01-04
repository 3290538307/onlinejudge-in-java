import java.util.concurrent.Callable;

public class TtlListener implements Callable<Boolean>{
	private volatile Process process;
	private final long timeout = 5000;
	private long starttime;
	private boolean timeLimited=false;
	private long deltatime=0;
        private static TtlListener instance=null;
		public TtlListener() {
		
	}
	public static TtlListener getInstance(){
		if(instance==null){
			instance=new TtlListener();
		}
		return instance;
	}
	public void setProcessAndStarttime(Process process,long starttime) {
		this.process = process;
		this.starttime=starttime;                                                                              
	}
	@Override
	public Boolean call() throws Exception {
		// TODO Auto-generated method stub
		timeLimited=false;
		while(process.isAlive()){
			deltatime=System.currentTimeMillis()-starttime;
			if(deltatime>timeout){//³¬Ê±
				//System.out.println(System.currentTimeMillis()-starttime+"ms");
				process.destroy();
				timeLimited=true;
				//System.out.println("³¬Ê±"+process.exitValue());
			}
		}    		
		return timeLimited;
	}
	public long getDeltatime(){
		return deltatime;
	}

}
