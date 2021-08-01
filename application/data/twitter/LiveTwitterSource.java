package application.data.twitter;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Encapsulates the connection to Twitter
 *
 * Terms to include in the returned tweets can be set with setFilterTerms
 *
 * Implements Observable - each received tweet is signalled to all observers
 */
public class LiveTwitterSource extends TwitterSource {
    private TwitterStream twitterStream;
    private StatusListener listener;

    public LiveTwitterSource() {
        initializeTwitterStream();
    }

    protected void sync() {
        FilterQuery filter = new FilterQuery();
        // https://stackoverflow.com/questions/21383345/using-multiple-threads-to-get-data-from-twitter-using-twitter4j
        String[] queriesArray = terms.toArray(new String[0]);
        filter.track(queriesArray);

        System.out.println("Syncing live Twitter stream with " + terms);

        twitterStream.filter(filter);
    }

    private void initializeListener() {
        listener = new StatusAdapter() {
            @Override
            public void onStatus(Status status) {
                // This method is called each time a tweet is delivered by the application.data.twitter API
                if (status.getPlace() != null) {
                    handleTweet(status);
                }
           }
        };
    }

    // Create ConfigurationBuilder and pass in necessary credentials to authorize properly, then create TwitterStream.
    private void initializeTwitterStream() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        TwitterAPI twitterAPI = new TwitterAPI();
        /* cb.setOAuthConsumerKey("VBuOwzrzuWfKcPkN9eYyUP9vK")
                .setOAuthConsumerSecret("gGIgqg86Y9qGc64UtlSiVkKDzbyBorEW2Cn6pC7LcjVwZKVLXp")
                .setOAuthAccessToken("70967537-2l7GSGWUTRhmKBDmZA6KhzsHM7mCY3M4takahapMG")
                .setOAuthAccessTokenSecret("9l0hpQNP0tbdSYrXwUFa2w5HYJRsYv2fjUO9ZIvbpYwet"); */

         cb.setOAuthConsumerKey(twitterAPI.getAuthConsumerKey())
                .setOAuthConsumerSecret(twitterAPI.getOAuthConsumerSecret())
                .setOAuthAccessToken(twitterAPI.getOAuthAccessToken())
                .setOAuthAccessTokenSecret(twitterAPI.getOAuthAccessTokenSecret());

        // Pass the ConfigurationBuilder in when constructing TwitterStreamFactory.
        twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
        initializeListener();
        twitterStream.addListener(listener);
    }
}