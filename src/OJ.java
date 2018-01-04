import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public interface OJ {
	//状态码
	public static final int def=0,ac=1,presentationError=2,wrongAnswer=3,compileError=8,timeLimitExceed=5,runTimeError=4,memoryLimitExceed=6;
	//对代码进行编译（非解释性语言），执行，对运行时间进行监听，返回测试用例状态码序列   baseMemory, processName, compareField
	public String executeCode(String code,Map<Integer, List<String>> data,ExecutorService service,TtlListener ttl,
	MemoryListener memOver,int language,String subId);
	//返回程序的各种错误信息
	public String getErrors();
	//向程序中输入参数
	public void inputData(OutputStream out,String[] data);
	//获取该程序执行完成后，返回的数据
	public String getOutputData(InputStream in);
	//返回本次判题的结果（状态码）
	public int getResult();
	//返回程序的运行时间
	public long getDeltaTime();
	//返回本次评判的正确率
	public float getAccuracy();
	//返回程序的运行内存
	public int getRunMemory();
	//设置语言，
	public void setLanguage(int language);
	public void convertCodeToFile(String code,String file);
}
