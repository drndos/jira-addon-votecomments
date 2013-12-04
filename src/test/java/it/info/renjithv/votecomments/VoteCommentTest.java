package it.info.renjithv.votecomments;

import com.atlassian.jira.functest.framework.FuncTestCase;


public class VoteCommentTest extends FuncTestCase {

    private String Plugin_Key = "info.renjithv.votecomments.jira-addon-votecomments";

    private String linkVoteUpImage = "images/icons/emoticons/thumbs_up.gif";

    @Override
    protected void setUpTest() {
        administration.restoreData("TestVoteCommentPlugin.zip");
        administration.plugins().enablePlugin(Plugin_Key);
    }

    public void testIfVoteUpIsVisible() {

        navigation.login("admin","admin");
        navigation.issue().viewIssue("TEST-1");
        //tester.clickLinkWithImage(linkVoteUpImage); Can't find the image.
        assertTrue("ID can't be found", locator.id("upvote").exists());
    }

}