
public class TwitterFriend {
	private long id;
	private String name;
	private String handle;
	private int onYourTimeline;
	private int favorites;
	private int retweetedBy;
	private int mentionsBy;
	private int friendScore;
	
	// Constructor
	public TwitterFriend() {
		this.id = 0;
		this.onYourTimeline = 0;
		this.favorites = 0;
		this.retweetedBy = 0;
		this.mentionsBy = 0;
		this.friendScore = 0;
	}
	
	public TwitterFriend(long id) {
		this.id = id;
		this.onYourTimeline = 0;
		this.favorites = 0;
		this.retweetedBy = 0;
		this.mentionsBy = 0;
		this.friendScore = 0;
	}
	
	// Setters
	public void setName(String name) {
		this.name = name;
	}
	
	public void setHandle(String handle) {
		this.handle = handle;
	}
	
	public void addOnYourTL() {
		this.onYourTimeline++;
		this.friendScore += 2;
	}
	
	public void addfavorite() {
		this.favorites++;
		this.friendScore++;
	}
	
	public void addRetweetedBy() {
		this.retweetedBy++;
		this.friendScore += 2;
	}
	
	public void addMentionedBy() {
		this.mentionsBy++;
		this.friendScore +=2;
	}
	
	public void setFriendScore(int score) {
		this.friendScore = score;
	}
	
	// Getters
	public long getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getHandle() {
		return this.handle;
	}
	
	public int getOnYourTL() {
		return this.onYourTimeline;
	}
	
	public int getFavorites() {
		return this.favorites;
	}
	
	public int getRetweetedBy() {
		return this.retweetedBy;
	}
	
	public int getMentionedBy() {
		return this.mentionsBy;
	}
	
	public int getFriendScore() {
		return this.friendScore;
	}
}
