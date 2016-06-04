package net.sharksystem.sharknet.api;

import java.util.List;

/**
 * Created by timol on 12.05.2016.
 *
 * Interface for the Chat Implementation - A Chat represents a Conversation with one or more contacts
 */
public interface Chat {

	/**
	 * Add a Message to the Chat and sends it
	 * @param content
     */
	public void sendMessage(Content content);

	/**
	 * deletes the Chat from the KnowledgeBase
	 */
	public void delete();

	/**
	 * returns a List of all Messages within the Chat
	 * @return
     */
    public List<Message> getMessages();

	/**
	 * Saves the chat in the Database
	 */
	public void save();

	/**
	 * Updates the chat in the Database
	 */
	public void update();

	/**
	 * Returns a List of the Contact wich are included in the Chat
	 * If there is just one Recipient the Method returns a List of the Size one
	 * @return
     */
	public List<Contact> getContacts();

	/**
	 * Set the Picture of the Chat
	 */
	public void setPicture(String picture);

	/**
	 * returns he chatpicture
	 * @return
     */
	public String getPicture();

	/**
	 * Set Title of he Chat. By Default its a String of all Nicknames of the Contact in the Chat
	 */
	public void setTitle(String title);

	/**
	 * Returns Title of the Chat
	 * @return
     */
	public String getTitle();

	/**
	 * Returns the ID of the Chat
	 */
	public int getID();

	/**
	 * Returns the Profile of the owner of the Chat
	 */
	public Profile getOwner();
}
