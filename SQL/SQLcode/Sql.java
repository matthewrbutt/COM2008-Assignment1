package SQLcode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import classPkg.Module;
import classPkg.PeriodOfStudy;
import classPkg.StuInfo;
import classPkg.UserInfo;

public class Sql {
	// find user
	public UserInfo checkLogIn(String usr, String pw) throws SQLException {
		Connection con = setUpConnection();
		int ID = 0;
		String pass = "";
		String perm = "";
		boolean logIn = false;
		PreparedStatement pstmt = null;
		UserInfo u = null;
		try {
			pstmt = con.prepareStatement("SELECT * FROM Users WHERE Username = ?");
			pstmt.setString(1, usr);
			ResultSet res = pstmt.executeQuery();
			while (res.next()) {
				ID = res.getInt(1);
				pass = res.getString(3);
				perm = res.getString(4);
				logIn = res.getBoolean(5);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			if (pstmt != null)
				pstmt.close();
		}
		if (con != null)
			con.close();
		// decryption of pass
		if ((pw.contentEquals(pass)) && (logIn == false)) {
			System.out.println("test");// change log in to true
			return new UserInfo(ID, perm);
		} else
			return null;
	}

	// find student info from regNo
	public StuInfo getStudentInfo(int reg) throws SQLException {
		Connection con = setUpConnection();
		StuInfo student = null;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement("SELECT * FROM Students WHERE RegNo = ?");
			pstmt.setInt(1, reg);
			ResultSet res = pstmt.executeQuery();
			while (res.next()) {
				String title = res.getString(2);
				String sur = res.getString(3);
				String fore = res.getString(4);
				String email = res.getString(5);
				String tutor = res.getString(6);
				String degree = res.getString(7);
				char p = res.getString(7).charAt(0);
				String awClass = res.getString(8);
				student = new StuInfo(reg, title, sur, fore, email, tutor, degree, p, awClass);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			if (pstmt != null)
				pstmt.close();
		}
		if (con != null)
			con.close();
		return student;
	}

	// returns array of modules taken this PoS
	public Module[] getModules(PeriodOfStudy p, StuInfo s) throws SQLException {
		Module[] modArray = new Module[10];
		Connection con = setUpConnection();
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		PreparedStatement pstmt3 = null;
		boolean ob = false;
		int credit = 0;
		char level = 'A';// default first value
		String taught = "";
		try {
			pstmt = con.prepareStatement("SELECT * FROM ModuleTaken WHERE PosRegCode = ?");
			pstmt2 = con.prepareStatement("SELECT * FROM ModuleAssignment WHERE ModuleCode = ? AND DegreeCode = ?");
			pstmt3 = con.prepareStatement("SELECT WhenTaught FROM Modules WHERE ModuleCode = ?");
			pstmt.setInt(1, p.getPosRegCode());
			ResultSet r1 = pstmt.executeQuery();
			while (r1.next()) {
				String mod = r1.getString(2);
				double grade = r1.getDouble(3);
				double resit = r1.getDouble(4);
				pstmt3.setString(1, mod);
				ResultSet r3 = pstmt3.executeQuery();
				while (r3.next()) {
					taught = r3.getString(1);
				}
				pstmt2.setString(1, mod);
				pstmt2.setString(2, s.getDegree());
				ResultSet r2 = pstmt2.executeQuery();
				while (r2.next()) {
					ob = r2.getBoolean(3);
					credit = r2.getInt(4);
					level = r2.getString(5).charAt(0);
				}
				modArray[modArray.length] = new Module(mod, s.getDegree(), ob, credit, level, taught, grade, resit);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			if (pstmt != null)
				pstmt.close();
			if (pstmt2 != null)
				pstmt2.close();
			if (pstmt3 != null)
				pstmt3.close();
		}
		if (con != null)
			con.close();
		return modArray;
	}

	public PeriodOfStudy[] getPeriodsOfStudy(int reg) throws SQLException {
		Connection con = setUpConnection();
		PeriodOfStudy[] posArray = new PeriodOfStudy[6];// can take 5 PoS's if taking masters with placement and can
														// only retake on PoS so 6 is max
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement("SELECT * FROM PeriodsOfStudy WHERE RegistrationNo = ?");
			pstmt.setInt(1, reg);
			ResultSet res = pstmt.executeQuery();
			while (res.next()) {
				int prcode = res.getInt(1);
				char pos = res.getString(3).charAt(0);
				Date start = res.getDate(4);
				Date end = res.getDate(5);
				char level = res.getString(6).charAt(0);
				double grade = res.getDouble(7);
				boolean progress = res.getBoolean(8);

				posArray[posArray.length] = new PeriodOfStudy(prcode, reg, pos, start, end, level, grade, progress);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (pstmt != null)
				pstmt.close();
		}
		if (con != null)
			con.close();
		return posArray;
	}

	// add fns
	// add new User
	public void addUser(int id, String usr, String pw, String perm) throws SQLException {
		Connection con = setUpConnection();
		PreparedStatement pstmt = null;
		// encrypt pw
		// generate id
		try {
			pstmt = con.prepareStatement("INSERT INTO Users VALUES (?,?,?,?,0)");
			pstmt.setInt(1, id);
			pstmt.setString(2, usr);
			pstmt.setString(3, pw);
			pstmt.setString(4, perm);
			pstmt.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			if (pstmt != null)
				pstmt.close();
		}
		if (con != null)
			con.close();
	}

	// add new department
	public void addDep(String code, String name) throws SQLException {
		Connection con = setUpConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement("INSERT INTO Departments VALUES (?,?)");
			pstmt.setString(1, code);
			pstmt.setString(2, name);
			pstmt.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			if (pstmt != null)
				pstmt.close();
		}
		if (con != null)
			con.close();
	}

	// add course
	public void addCourse(String code, String name, String dep, String level, boolean pl) throws SQLException {
		Connection con = setUpConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement("INSERT INTO Degrees VALUES (?,?,?,?,0)");
			pstmt.setString(1, code);
			pstmt.setString(2, name);
			pstmt.setString(3, dep);
			pstmt.setString(4, level);
			pstmt.setBoolean(5, pl);
			pstmt.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			if (pstmt != null)
				pstmt.close();
		}
		if (con != null)
			con.close();
	}

	// add module
	public void addModule(String modCode, String modName, String whenTaught) throws SQLException {
		Connection con = setUpConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement("INSERT INTO Module VALUES (?,?,?)");
			pstmt.setString(1, modCode);
			pstmt.setString(2, modName);
			pstmt.setString(3, whenTaught);
			pstmt.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			if (pstmt != null)
				pstmt.close();
		}
		if (con != null)
			con.close();
	}

	// add student
	public void addStudent(int regNo, String title, String surname, String forename, String email, String tutor,
			String degCode, char periodOfStudy, String awardedClass) throws SQLException {
		Connection con = setUpConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement("INSERT INTO Students VALUES (?,?,?,?,?,?,?,?,?)");
			pstmt.setInt(1, regNo);
			pstmt.setString(2, title);
			pstmt.setString(3, surname);
			pstmt.setString(4, forename);
			pstmt.setString(5, email);
			pstmt.setString(6, tutor);
			pstmt.setString(7, degCode);
			pstmt.setString(8, String.valueOf(periodOfStudy));
			pstmt.setString(9, awardedClass);
			pstmt.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			if (pstmt != null)
				pstmt.close();
		}
		if (con != null)
			con.close();
	}

	// add taken module
	public void addTakenModule(int regCode, String modCode, double g, double r) throws SQLException {
		Connection con = setUpConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement("INSERT INTO ModuleTaken VALUES (?,?,?,?)");
			pstmt.setInt(1, regCode);
			pstmt.setString(2, modCode);
			pstmt.setDouble(3, g);
			pstmt.setDouble(4, r);
			pstmt.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			if (pstmt != null)
				pstmt.close();
		}
		if (con != null)
			con.close();
	}

	// remove fns

	// delete user
	public void removeUser(int id) throws SQLException {
		Connection con = setUpConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement("DELETE FROM Users WHERE ID = ?");
			pstmt.setInt(1, id);
			pstmt.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			if (pstmt != null)
				pstmt.close();
		}
		if (con != null)
			con.close();
	}
	// delete department

	public void removeDeps(String dep) throws SQLException {
		Connection con = setUpConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement("DELETE FROM Departments WHERE DepartmentCode = ?");
			pstmt.setString(1, dep);
			pstmt.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			if (pstmt != null)
				pstmt.close();
		}
		if (con != null)
			con.close();
	}

	// delete course
	public void removeCourse(String deg) throws SQLException {
		Connection con = setUpConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement("DELETE FROM Degrees WHERE DegreeCode = ?");
			pstmt.setString(1, deg);
			pstmt.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			if (pstmt != null)
				pstmt.close();
		}
		if (con != null)
			con.close();
	}

	// delete module
	public void removeMod(String mod) throws SQLException {
		Connection con = setUpConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement("DELETE FROM Modules WHERE ModuleCode = ?");
			pstmt.setString(1, mod);
			pstmt.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			if (pstmt != null)
				pstmt.close();
		}
		if (con != null)
			con.close();
	}
	// delete student

	public void removeStudent(int reg) throws SQLException {
		Connection con = setUpConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement("DELETE FROM Students WHERE RegistrationNo = ?");
			pstmt.setInt(1, reg);
			pstmt.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			if (pstmt != null)
				pstmt.close();
		}
		if (con != null)
			con.close();
	}

	// drop taken module
	public void removeTakenMod(int regCode, String modCode) throws SQLException {
		Connection con = setUpConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement("DELETE FROM ModuleTaken WHERE PosRegCode = ? AND ModuleCode = ?");
			pstmt.setInt(1, regCode);
			pstmt.setString(2, modCode);
			pstmt.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			if (pstmt != null)
				pstmt.close();
		}
		if (con != null)
			con.close();
	}

	// update fns
	// update awarded class
	public void updateAwardedClass(int regNo, String awardedClass) throws SQLException {
		Connection con = setUpConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement("UPDATE Students SET AwardedClass = ? WHERE RegistrationNo = ?");
			pstmt.setString(1, awardedClass);
			pstmt.setInt(2, regNo);
			pstmt.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			if (pstmt != null)
				pstmt.close();
		}
		if (con != null)
			con.close();
	}

	// update grade (ask for grade type in param, norm or resit)
	public void updateGrade(int posRegNo, double grade) throws SQLException {
		Connection con = setUpConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement("UPDATE PeriodsOfStudy SET Grade = ? WHERE PosRegCode = ?");
			pstmt.setDouble(1, grade);
			pstmt.setInt(2, posRegNo);
			pstmt.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			if (pstmt != null)
				pstmt.close();
		}
		if (con != null)
			con.close();
	}

	public void updateModuleGrade(int posRegNo, String mod, double grade) throws SQLException {
		Connection con = setUpConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement("UPDATE PeriodsOfStudy SET Grade = ? WHERE PosRegCode = ? AND ModuleCode = ?");
			pstmt.setDouble(1, grade);
			pstmt.setInt(2, posRegNo);
			pstmt.setString(3, mod);
			pstmt.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			if (pstmt != null)
				pstmt.close();
		}
		if (con != null)
			con.close();
	}

	// add resit grade for module
	public void updateModuleResit(int posRegNo, String mod, double grade) throws SQLException {
		Connection con = setUpConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement("UPDATE PeriodsOfStudy SET Resit = ? WHERE PosRegCode = ? AND ModuleCode = ?");
			pstmt.setDouble(1, grade);
			pstmt.setInt(2, posRegNo);
			pstmt.setString(3, mod);
			pstmt.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			if (pstmt != null)
				pstmt.close();
		}
		if (con != null)
			con.close();
	}

	// update progress fn
	public void updateProgress(int posRegNo, boolean b) throws SQLException {
		Connection con = setUpConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement("UPDATE PeriodsOfStudy SET Progress = ? WHERE PosRegCode = ?");
			pstmt.setBoolean(1, b);
			pstmt.setInt(2, posRegNo);
			pstmt.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			if (pstmt != null)
				pstmt.close();
		}
		if (con != null)
			con.close();
	}

	// other fns
	// check taken module are applicable (right level of study and module)
	// check taken module credit total (could leave for josh and tom)
	// calc weighted mean grade (could leave for tom and josh as no sql involved
	// other than getting all PoS grades)
	// pass or fail (same as above, we just need a fn to return PoSmodulesinfo like
	// find module)
	// find awarded class in final year (as above)

	// get connection
	public Connection setUpConnection() throws SQLException {
		Connection con = null;
		try {
			con = DriverManager.getConnection("jdbc:mysql://stusql.dcs.shef.ac.uk/team035", "team035", "b7af5f37");
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return con;
	}
}