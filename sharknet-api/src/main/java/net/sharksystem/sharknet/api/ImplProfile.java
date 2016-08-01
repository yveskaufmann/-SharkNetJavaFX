package net.sharksystem.sharknet.api;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by timol on 16.05.2016.
 */
public class ImplProfile implements Profile {

	Contact c;
	String password ="";
	Setting setting;
	Blacklist blacklist;
	ScheduleWeek scheduleWeek = null;
	List<Lesson> lessons = new ArrayList<>();


	/**
	 * Constructor for new Profiles which are going to be saved
	 * @param nickname
     */
	public ImplProfile(String nickname, String deviceID){
		c = new ImplContact(nickname, gernateUID(nickname, deviceID), "", this);
		setting = new ImplSetting(this);
		save();
	}

	/**
	 * Constructor for the Database
	 * @param c
	 * @param password
	 * @param setting
	 * @param blacklist
     */
	public ImplProfile(Contact c, String password, Setting setting, Blacklist blacklist){
		this.c = c;
		this.password = password;
		this.setting = setting;
		this.blacklist = blacklist;
	}


	@Override
	public Contact getContact() {
		return c;
	}

	@Override
	public void setContact(Contact c) {
		this.c = c;
	}

	@Override
	public Setting getSettings() {
		if(setting == null) setting = new ImplSetting(this);
		return setting;
	}

	@Override
	public void delete() {

		//ToDo: Shark - delete the Profile in the KB
		//Implementation of DummyDB
		DummyDB.getInstance().removeProfile(this);
	}

	@Override
	public void save() {

		//ToDo: Shark - saveProfile in the KB
		//Implementation of DummyDB
		DummyDB.getInstance().addProfile(this);
	}

	@Override
	public void update() {
		//ToDo: Shark - update Profile in the KB
	}

	@Override
	public ScheduleWeek getScheduleWeek() {
		return scheduleWeek;
	}

	@Override
	public void setScheduleWeek(ScheduleWeek scheduleWeek) {
		this.scheduleWeek = scheduleWeek;
		save();
	}

	@Override
	public List<Lesson> getLessons() {
		return lessons;
	}

	@Override
	public void addLesson(Lesson lesson) {
		this.lessons.add(lesson);
	}

	@Override
	public boolean deleteLesson(int index) {
		if (lessons.size() == 0) {
			return false;
		} else {
			int count = lessons.size();
			lessons.remove(index);
			return count > lessons.size();
		}
	}

	@Override
	public boolean login(String password) {
		if(this.password.equals(password)){
			return true;
		}
		else return false;
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
	}


	@Override
	public boolean isEqual(Profile p){
		if(p.getContact().isEqual(c)) return true;
		else return false;
	}

	@Override
	public Blacklist getBlacklist() {
		if(blacklist == null){
			blacklist = new ImplBlacklist(this);
		}
		return blacklist;
	}

	@Override
	public void renewKeys() {
		// Generate a new dummy public key
		if (getContact() instanceof ImplContact) {
			DummyKeyPairHelper.createNewKeyForContact((ImplContact) getContact());
		}
		//ToDo: Shark - Renew the keypair
	}

	private String gernateUID(String nickname, String deviceID){
		String newUID = "foo";
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss:SS");
		Date now = new Date(System.currentTimeMillis());
		String strDate = sdfDate.format(now);

		newUID = nickname + "/" + deviceID + "/" + strDate;
		return newUID;
	}


}
