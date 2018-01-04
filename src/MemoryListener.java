import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.util.concurrent.Callable;
import java.io.InputStreamReader;
public class MemoryListener implements Callable<Boolean>{
	//DEFINE_int32(cppbase_memory, 7152, "c++语言最基本需要内存大小");
	//DEFINE_int32(cbase_memory, 1820, "c语言最基本需要内存大小");
	//DEFINE_int32(javabase_memory, 13900, "java最基本需要内存大小");
	private final long limitMemory=50240;//单位：kb
	private int baseMemory;
	private boolean memLimit;
	private volatile Process process;
	//private int pid;    //进程号
	private String processName,compareField;//c c++
	private int memory;
	private static MemoryListener instance=null;
	public MemoryListener(){}
	public static MemoryListener getInstance(){
		if(instance==null){
			instance=new MemoryListener();
		}
		return instance;
	}
	public void setInfo(Process process,int baseMemory,String processName,String compareField) {
		this.process = process;
		this.baseMemory=baseMemory;
		this.processName=processName;
		this.compareField=compareField;
	}
	private String getPid() throws InterruptedException{
		String content=null;
		try {
			String[] cmds = {"/bin/sh","-c","ps -ef|grep "+processName};
			//System.out.println(processName);
			Process process=Runtime.getRuntime().exec(cmds);
			process.waitFor();
			BufferedReader reader=new BufferedReader(new InputStreamReader(process.getInputStream()));
			content=reader.readLine();
			reader.close();
		} catch (IOException e) {
			//	e.printStackTrace();
		}
		return content.split("\\s+")[1];
	}
	private int getProcessMem()throws InterruptedException{
		int memory=0;
		try {
			File file=new File("/proc/"+getPid()+"/status");
			BufferedReader reader=null;
			if(file.exists()) {
				reader=new BufferedReader(new FileReader(file));
			}
			String content=null;
			if(reader!=null) {
				while((content=reader.readLine())!=null){
					if(content.indexOf(compareField)!=-1){
						break;
					}
				}
			}
			if(content!=null)
				memory=Integer.valueOf(content.split("\\s+")[1]);
			if(reader!=null)
				reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return memory;

	}
	@Override
	public Boolean call() throws Exception {
		memLimit=false;memory=0;                         //c++
		int tempMemory=0,currentMemory=0;
		while(process.isAlive()){
			currentMemory=getProcessMem();
			if(currentMemory>tempMemory){
				tempMemory=currentMemory;
			}
			if(tempMemory>limitMemory){

				process.destroy();
				memLimit=true;
			}
		}
		if(tempMemory-baseMemory>0&&!memLimit){
			//	tempMemory=0;
			tempMemory-=baseMemory;
		}
		memory=tempMemory;
		return memLimit;
	}
	public int getMemory(){
		return memory;
	}
	// javac -encoding gbk Main.java -classpath /home/user/JAVAProject/QOJ/bin/
}
