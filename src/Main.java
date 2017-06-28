import java.util.*;

import twitter4j.IDs;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.UserMentionEntity;
import twitter4j.conf.ConfigurationBuilder;

public class Main {

	public static void main(String[] args) throws TwitterException {
		
		String[] credentials = getCredentials();
		
		System.out.println("Verifying credentials...");
		ConfigurationBuilder confBuilder = new ConfigurationBuilder();
		confBuilder.setDebugEnabled(true)
			.setOAuthConsumerKey(credentials[0])
			.setOAuthConsumerSecret(credentials[1])
			.setOAuthAccessToken(credentials[2])
			.setOAuthAccessTokenSecret(credentials[3]);

		TwitterFactory twitFactory = new TwitterFactory(confBuilder.build());
		twitter4j.Twitter twitter = twitFactory.getInstance();
		
		// Getting Friends
		List<TwitterFriend> friends = getMutualFriends(twitter);
		
		// Counting Favorites
		countFavorites(friends, twitter);
		
		// Counting Retweets
		countRetweets(friends, twitter);

		// Counting Mentions
		countMentions(friends, twitter);
		
		// Counting user's mentions
		readTimeline(friends, twitter);
		
		// Get top friends
		TwitterFriend[] topFriends = getTopFriends(friends);
		
		// Get names and handles
		getIDinfo(topFriends, twitter);
		
		// Print Results
		printResults(topFriends);
		
	}
	
	@SuppressWarnings("resource")
	public static String[] getCredentials() {
		System.out.println("Please input your credentials from the Twitter development site.");
		Scanner input = new Scanner(System.in);
		String[] credentials = new String[4];
		
		System.out.print("Consumer Key (API Key): ");
		credentials[0] = input.nextLine();
		
		System.out.print("Consumer Secret (API Secret): ");
		credentials[1] = input.nextLine();
		
		System.out.print("Access Token: ");
		credentials[2] = input.nextLine();
		
		System.out.print("Access Token Secret: ");
		credentials[3] = input.nextLine();
		
		return credentials;
	}
	
	public static List<TwitterFriend> getMutualFriends(twitter4j.Twitter twitter) throws TwitterException {
		System.out.println("Getting followers...");
		IDs followerIds = twitter.getFollowersIDs(-1);
		long[] followers = followerIds.getIDs();
		
		System.out.println("Getting people you follow...");
		IDs followingIds = twitter.getFriendsIDs(-1);
		long[] following = followingIds.getIDs();
		
		List<TwitterFriend> friends = new ArrayList<TwitterFriend>();
		
		for(int i=0; i<following.length; i++) {
			for(int j=0; j<followers.length; j++) {
				if (following[i]==followers[j]) {
					friends.add(new TwitterFriend(following[i]));
				}
			}
		}
		
		return friends;
	}
	
	public static void countFavorites(List<TwitterFriend> friends, twitter4j.Twitter twitter) throws TwitterException {
		System.out.println("Counting tweets you favorited...");
		
		List<Status> favorites = new ArrayList<Status>();
		
		for (int i=1; i<5; i++) {
			Paging page = new Paging(i, 50);
			List<Status> newFavorites = twitter.getFavorites(page);
			favorites.addAll(newFavorites);
			
		}	
		
		for (Status favorite: favorites) {
			long id = favorite.getUser().getId();
			for (TwitterFriend friend: friends) {
				if (friend.getId()==id) {
					friend.addfavorite();
				}
			}
		}
		
	}
	
	public static void countRetweets(List<TwitterFriend> friends, twitter4j.Twitter twitter) throws TwitterException {
		System.out.println("Counting your retweeted tweets...");
		
		Paging page1 = new Paging(1,20);
		Paging page2 = new Paging(2,20);
		List<Status> retweets = twitter.getRetweetsOfMe(page1);
		retweets.addAll(twitter.getRetweetsOfMe(page2));
		
		for (Status retweet: retweets) {
			long[] retweeters = twitter.getRetweeterIds(retweet.getId(), -1).getIDs();
			
			for (long retweeter: retweeters) {
				for(TwitterFriend friend: friends) {
					if (friend.getId()==retweeter) {
						friend.addRetweetedBy();
					}
				}
			}
			
		}
	}
	
	public static void countMentions(List<TwitterFriend> friends, twitter4j.Twitter twitter) throws TwitterException {
		System.out.println("Counting mentions of you...");
		
		Paging page1 = new Paging(1,20);
		Paging page2 = new Paging(2,20);
		List<Status> mentions = twitter.getMentionsTimeline(page1);
		mentions.addAll(twitter.getMentionsTimeline(page2));
		
		for (Status mention: mentions) {
			long id = mention.getUser().getId();
			for(TwitterFriend friend: friends) {
				if (friend.getId() == id) {
					friend.addMentionedBy();
				}
			}
		}
	}
	
	public static void readTimeline(List<TwitterFriend> friends, twitter4j.Twitter twitter) throws TwitterException {
		System.out.println("Reading your timeline...");
		
		List<Status> userTimeline = new ArrayList<Status>();
		for (int i=1; i<6; i++) {
			Paging page = new Paging(i, 50);
			List<Status> newTimeline = twitter.getUserTimeline(page);
			userTimeline.addAll(newTimeline);	
		}

		for(Status tweet: userTimeline) {
			UserMentionEntity[] mentionEntities = tweet.getUserMentionEntities();
			for (UserMentionEntity entity: mentionEntities) {
				long id = entity.getId();
				for (TwitterFriend friend: friends) {
					if (friend.getId() == id) {
						friend.addOnYourTL();
					}
				}
			}
		}
	}
	
	public static void getIDinfo(TwitterFriend[] friends, twitter4j.Twitter twitter) throws TwitterException {
		System.out.println("Loading leaderboard...");
		
		for(int i=0; i<friends.length; i++) {
			User friendProfile = twitter.showUser(friends[i].getId());
			friends[i].setName(friendProfile.getName());
			friends[i].setHandle(friendProfile.getScreenName());
		}
	}
	
	public static TwitterFriend[] getTopFriends(List<TwitterFriend> friends) {
		TwitterFriend[] topFriends = new TwitterFriend[10];
		for (int i=0; i<topFriends.length; i++) {
			int topScore = 0;
			TwitterFriend topFriend = null;
			for (TwitterFriend friend: friends) {
				if(friend.getFriendScore() > topScore) {
					topScore = friend.getFriendScore();
					topFriend = friend;
				}
			}
			topFriends[i] = topFriend;
			friends.remove(topFriend);
		}
		return topFriends;
	}
	
	public static void printResults(TwitterFriend[] topFriends) {
		System.out.println();
		for (int i=0; i<topFriends.length; i++) {
			System.out.println(
							(i+1)
//							+"\tID: "+topFriends[i].getId()
//							+"\tName: "+topFriends[i].getName()
							+"\t@"+topFriends[i].getHandle()
							+"\tScore: "+topFriends[i].getFriendScore()
//							+"\tRetweetedBy: "+topFriends[i].getRetweetedBy()
//							+"\tMentionsBy: "+topFriends[i].getMentionedBy()
//							+"\tFavorites: "+topFriends[i].getFavorites()
//							+"\tOnYourTL: "+topFriends[i].getOnYourTL()
							);
		}
	}

}
