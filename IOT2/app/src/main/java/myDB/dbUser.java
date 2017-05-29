package myDB;


import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.cwsu.iot.User;

public class dbUser {
	
	private  SQLiteDatabase db;
	
	public dbUser(iotDB database){
		db = database.db;
	}

	private boolean checkDB(){
		if(db.isOpen() == true){
			return true;
		}
		else{
			System.out.println("Database not opened");
			return false;
		}
	}

	public boolean addUser(String username , String password , String key)
	{
		if(checkDB())
		{
			String SQL = "INSERT OR IGNORE INTO user(username,password,key)"
					+ " VALUES(" + "'" + username + "'" +","+
								   "'" + password + "'" +","+ 
					               "'" + key + "'" + ")";

			return true;
		}
		else
		{
			return false;
		}
	}

	//get key by username
	public User getUserByUN(String username){
		if(checkDB())
		{
			String where = "username" + "=" + username;
			User result = null;
			Cursor cursor = db.query
					("user",null,where,null,null,null,null,null);
			if(cursor.moveToNext())
			{
				result = setUser(cursor);
				cursor.close();
				return result;
			}
			else
			{
				System.out.println("Data not found");
				cursor.close();
				return null;
			}
		}
		else
		{
			return null;
		}
	}

	//�ǥ�ID�R���ϥΪ�
	public boolean deleteByID(String id){
		if(checkDB()){
			if(getUserByUN(id) != null)
			{
				String where = "user_id" + "=" + id;
				db.delete("user", where, null);
				return true;
			}
			else{
				return false;
			}
		}
		else
		{
			return false;
		}

	}
	//��s�ϥΪ̸��
	public boolean updateByID(String id , User updateUser){
		if(checkDB())
		{
			String where = "username" + "=" + id;
			ContentValues cv = new ContentValues();
			cv.put("username", updateUser.getUsername());
			cv.put("password", updateUser.getPassword());
			cv.put("key", updateUser.getKey());
			db.update("user", cv, where, null);
			return true;
		}
		else
		{
			return false;
		}
	}

	//���o�ϥΪ̼ƶq
	public int getUserCount(){
		if(checkDB())
		{
			int result = 0;
			Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + "user", null);
			if(cursor.moveToNext()){
				result = cursor.getInt(0);
			}
			else{
				System.out.println("User not found");
				return -1;
			}
			cursor.close();
			return result;
		}
		else
		{
			return -1;
		}
		
	}
	//confirm there is a user or not
	public User checkUser(String username , String password){
		if(checkDB())
		{
			String where = "username = ? AND password = ?";
			String[] columns = {username , password};
			Cursor result = db.query("user",null ,where,columns , null, null, null);
			if(result.getCount() != 0 && result.getCount() == 1)
			{
				Log.i("check_user","find user");
				return setUser(result);
			}
			else
			{
				Log.i("check_user","user not found");
				return null;
			}
		}
		else
		{
			System.out.println("database not opened");
			return null;
		}
		
	}
	//�ϥΪ̬O�_����
	public boolean checkUserAccountIsUnique(String account){
		
		String where = "account = ? ";
		String[] columns = {account};
		Cursor result = db.query("user",null ,where,columns , null, null, null);
		
		if(result.getCount() == 0){
			return true;
		}
		else{
			return false;
		}
	}

	//�ʸ˨ϥΪ�
	private User setUser(Cursor result)
	{
		result.moveToFirst();
		User temp = new User();
		temp.setUsername(result.getString(0));
		temp.setPassword(result.getString(1));
		temp.setKey(result.getString(2));
		return temp;
	}
}
