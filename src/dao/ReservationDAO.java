package dao;

import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import theater.Reservation;

// ��ȭ ���� DAO
public class ReservationDAO {
	List<Reservation> resList;
	
	String jdbcUrl = "jdbc:mysql://localhost:3306/theater?useSSL=false";
	String dbId = "parkyoonjung";;
	String dbPass = "qkrdbswjd";

	HashMap<Integer,Integer> chart;
	Connection conn;
	PreparedStatement pstmt;
	ResultSet rs;
	
	private void connectDB() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(jdbcUrl, dbId, dbPass);
		} catch (Exception e) {

			e.printStackTrace();
		}
		
	}
	
	private void disConnectDB() {
		if(rs!= null) try {rs.close();}catch (Exception e) {}
		if(pstmt != null) try{pstmt.close();}catch(SQLException sqle){}
		if(conn != null) try{conn.close();}catch(SQLException sqle){}
	}
	
	public HashMap<Integer,Integer> getMovieChart() {
		connectDB();
		chart = new HashMap<>();
		String sql = "SELECT movieNo FROM reservation";
		
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				int key = rs.getInt(1);
				if(chart.containsKey(key)) { // ��Ʈ�� ��ȭ�� �̹� �ִٸ� �߰� ����
					chart.put(key, chart.get(key) + 1);
				}else { // ��Ʈ�� ��ȭ�� �����Ƿ� �߰�
					chart.put(key, 1);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		disConnectDB();
		return chart;
	}
	
	public String isPayment(String resNo) { // �������θ� �Ǵ���
		connectDB();
		String sql = "select ispayment from reservation where resNo =?"; // ���Ź�ȣ�� �������� ��������
		String check = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, resNo);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				check = rs.getString("ispayment");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		disConnectDB();
		return check;
	}
	
	public boolean EndPayment(String resNo) { // �������θ� true�� ��ȯ
		connectDB();
		String sql = "update reservation set ispayment = ? where resNo =?"; // ���Ź�ȣ�� �������� ��������
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, "true");
			pstmt.setString(2, resNo);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		disConnectDB();
		return true;
	}
	
	// ���̵� ���� ���� ���� ����Ʈ ��ȯ
	public List<Reservation> getPaymentListOfID(String id){
		 connectDB();
		 String sql = "select * from reservation where memberId =?"; // ���̵�� ���ų��� ��������
		 resList = new ArrayList<>();
			try {
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, id);
				rs = pstmt.executeQuery();
				while(rs.next()) {
				Reservation res = new Reservation(id);
				res.setResNo(rs.getString("resNo"));
				res.setId(rs.getString("memberId"));
				res.setSeatNo(rs.getInt("seatNo"));
				res.setMovieNo(rs.getInt("movieNo"));
				res.setMovieSchedule(rs.getInt("movieSchedule"));
				res.setBookingTime(rs.getString("bookingTime"));
				res.setBookingDay(rs.getString("bookingDay"));
				res.setBranchNo(rs.getInt("branchNo"));
				res.setScreenNum(rs.getInt("screenNum"));
				res.setPrice(rs.getInt("price"));
				res.setIspayment(rs.getString("ispayment"));
				resList.add(res);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			disConnectDB();
		 return resList;
	}
	
	
	
	// ���� ���� ���̺��� Ȯ���ؼ� ������ ���� ��ȣ�� ��ȯ�ϴ� �Լ� (�����-���Ź�ȣ-�������)
	public int getFinalResNo() {
		connectDB();
		String sql ="SELECT substring_index(substring_index(resNo, '-', 2), '-', -1) FROM reservation "
				+ "order by substring_index(substring_index(resNo, '-', 2), '-', -1) desc";
		int final_resNo = 0;
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			if(!rs.next()) {
				return 0;
			}
			
			final_resNo = Integer.parseInt(rs.getString(1));
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
				
		
		disConnectDB();
		
		return final_resNo;
	
	}
	
	
	// ��ȭ ������ �����Ѵ� �Լ�. ���� ������ ���������� �߰��Ѵ�.
	public boolean doReservation(Reservation reservation) {
		connectDB();
		String sql = "INSERT INTO reservation(resNo, memberId,  seatNo, movieNo, movieSchedule, "
				 + "bookingTime, bookingDay, branchNo, screenNum, price, ispayment)"
				 +  "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		boolean success = true;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, reservation.getResNo());
			pstmt.setString(2, reservation.getId());
			pstmt.setInt(3, reservation.getSeatNo());
			pstmt.setInt(4, reservation.getMovieNo());
			pstmt.setInt(5, reservation.getMovieSchedule());
			pstmt.setString(6, reservation.getBookingTime());
			pstmt.setString(7, reservation.getBookingDay());
			pstmt.setInt(8, reservation.getBranchNo());
			pstmt.setInt(9, reservation.getScreenNum());
			pstmt.setInt(10, reservation.getPrice());
			pstmt.setString(11, reservation.getIspayment());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			success = false;
			e.printStackTrace();
		}
		disConnectDB();
		
		return true;
	}
	
	
	// ������ �Ϸ�� �¼����� ����Ʈ�� ��ȯ
	public List<Integer> getReservedSeatList(int screeningtableNo){
		connectDB();
		List<Integer> reserved_seat_list = new ArrayList<>();
		String sql = "SELECT seatNo FROM reservation WHERE movieSchedule = ?";
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, screeningtableNo);
			rs = pstmt.executeQuery();
			
			if(!rs.next()) {
				return reserved_seat_list;
			}
			
			do {
				reserved_seat_list.add(rs.getInt("seatNo"));
			}while(rs.next());
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		disConnectDB();
		return reserved_seat_list;
	}
	
	
	public boolean cancelReservation(String resNo) {
		connectDB();
		String sql = "DELETE FROM reservation WHERE resNo = ?";
		boolean success = true;
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, resNo);
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			success = false;
			e.printStackTrace();
		}
	
		disConnectDB();
		return success;
	}

	
}