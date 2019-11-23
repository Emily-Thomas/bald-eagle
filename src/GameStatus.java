import java.io.File;
import java.io.Serializable;

/**
* Class containing necessary output information for CPSC 4900 Solitaire Program
*
* @author Mason T. Yost
* @version 1.0
* @since 10/12/19
**/
public class GameStatus implements Serializable
{
	// Instance Variables
	private int gameStatusFlag;
	private int gameScore;
	private int gameTime; //seconds
	private File gameSaveFile;
	
	/**
	 * Creates a new gameStatus object using provided flag, score, time, and file object
	 * 
	 * @param statusFlag     game status flag: 0 = loss, 1 = forfeit, 2 = win, > 2 = pause, < 0 = error
	 * @param score          game score
	 * @param time           game time in seconds
	 * @param saveFile       java.io.File object for saving the game state          
	 */
	public GameStatus(int statusFlag, int score, int time, File saveFile)
	{
		gameStatusFlag = statusFlag;
		gameScore = score;
		gameTime = time;
		gameSaveFile = saveFile;
	}
	
	/**
	 * Creates a new gameStatus object using provided flag, score, time, and file path as a string
	 * 
	 * @param statusFlag     game status flag: 0 = loss, 1 = forfeit, 2 = win, &gt;2 = pause, &lt;0 = error
	 * @param score          game score
	 * @param time           game time in seconds
	 * @param saveFilePath   String object with the path of the file for the file object          
	 */
	public GameStatus(int statusFlag, int score, int time, String saveFilePath)
	{
		gameStatusFlag = statusFlag;
		gameScore = score;
		gameTime = time;
		gameSaveFile = new File(saveFilePath);
	}
	
	/**
	 * Creates a new gameStatus object using provided flag, score, and time. File is set to null
	 * 
	 * @param statusFlag     game status flag: 0 = loss, 1 = forfeit, 2 = win, &gt;2 = pause, &lt;0 = error
	 * @param score          initial score
	 * @param time           game time in seconds
	 */
	public GameStatus(int statusFlag, int score, int time)
	{
		gameStatusFlag = statusFlag;
		gameScore = score;
		gameTime = time;
		gameSaveFile = null;
	}
	
	/**
	 * Creates a new gameStatus object with placeholder values or nulls for private variable values.
	 */
	public GameStatus()
	{
		gameStatusFlag = -1;
		gameScore = -1;
		gameTime = -1;
		gameSaveFile = null;
	}
	
	/**
	 * Setter for gameStatusFlag value
	 * 
	 * @param status         new value for gameStatusFlag
	 */
	public void setGameStatusFlag(int status)
	{
		gameStatusFlag = status;
	}
	
	/**
	 * Getter for the gameStatusFlag value
	 *
	 * @return                current value of gameStatusFlag variable
	 */
	public int getGameStatusFlag()
	{
		return gameStatusFlag;
	}
	
	/**
	 * Setter for gameScore value
	 * 
	 * @param score         new value for gameScore
	 */
	public void setGameScore(int score)
	{
		gameScore = score;
	}
	
	/**
	 * Getter for the gameScore value
	 *
	 * @return                current value of gameScore variable
	 */
	public int getGameScore()
	{
		return gameScore;
	}
	
	/**
	 * Setter for gameTime value
	 * 
	 * @param time         new value for gameTime
	 */
	public void setGameTime(int time)
	{
		gameTime = time;
	}
	
	/**
	 * Getter for the gameTime value
	 *
	 * @return                current value of gameTime variable
	 */
	public int getGameTime()
	{
		return gameTime;
	}
	
	/**
	 * Setter for gameSaveFile object
	 * 
	 * @param saveFile        File object for gameSaveFile to point to (DOES NOT MAKE A NEW COPY)
	 */
	public void setGameSaveFile(File saveFile)
	{
		gameSaveFile = saveFile;
	}
	
	/**
	 * Setter for gameSaveFile object
	 * 
	 * @param saveFile       object for gameSaveFile to copy (DOES MAKE A NEW COPY)
	 */
	public void setNewGameSaveFile(File saveFile)
	{
		gameSaveFile = new File(saveFile.getPath());
	}
	
     /**
	 * Setter for gameSaveFile object
	 * 
	 * @param saveFilePath   String of the file path for the java file obj.
	 */
	public void setGameSaveFileFromStr(String saveFilePath)
	{
		gameSaveFile = new File(saveFilePath);
	}
	
	/**
	 * Getter for the gameStatusFlag object
	 *
	 * @return                current value of gameStatusFlag variable
	 */
	public File getGameSaveFile()
	{
		return gameSaveFile;
	}
}