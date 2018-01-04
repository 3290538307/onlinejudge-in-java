import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class SubmitCodeDao {
	private Connection conn;
	
	public SubmitCodeDao() {
	}
	
	public SubmitCodeDao(Connection conn) {
		this.conn=conn;
	}
	public void setConnection(Connection conn){
		this.conn=conn;
	}
	public void update(Map<Integer, List<Object>> collection){
		String sql="update teach_submit_code set submit_state=?,submit_error_message=?,test_state=?,submit_time=?,accuracy=? where id=?";
		try {
			PreparedStatement pstmt=conn.prepareStatement(sql);
			for(int i:collection.keySet()){
				List<Object> row=collection.get(i);
				for(int j=0;j<row.size();j++){
					pstmt.setObject(j+1, row.get(j));
				}
				pstmt.addBatch();
			}
			pstmt.executeBatch();
			pstmt.clearBatch();
			pstmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public int update(int id,int submit_state,String errorMsg,String testStates,int submit_time,float accuracy,int submit_memory) throws SQLException{
		String sql="update teach_submit_code set submit_state=?,submit_error_message=?,test_state=?,submit_time=?,accuracy=?,submit_memory=? where id=?";
		if(testStates.equals("")){
			testStates="0";
		}
		PreparedStatement pstmt=conn.prepareStatement(sql);
		pstmt.setInt(1, submit_state);
		pstmt.setString(2, errorMsg);
		pstmt.setString(3, testStates);
		pstmt.setInt(4, submit_time);
		pstmt.setFloat(5, accuracy);
		pstmt.setInt(6, submit_memory);
		pstmt.setInt(7, id);
		int res=pstmt.executeUpdate();
		pstmt.close();
		return res;
	}
}
