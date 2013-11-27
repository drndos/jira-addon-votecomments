package it.info.renjithv.votecomments.pageobjects;



import com.atlassian.jira.pageobjects.pages.AbstractJiraPage;
import com.atlassian.pageobjects.elements.ElementBy;
import com.atlassian.pageobjects.elements.PageElement;
import com.atlassian.pageobjects.elements.query.TimedCondition;
import org.openqa.selenium.By;

import javax.annotation.Nonnull;

public class VoteCommentsPage extends AbstractJiraPage {

    private static final String URI = "/browse/TEST-1";

    @ElementBy(id= "upvote")
    protected PageElement voteUpButton;

    @ElementBy(id = "downvote")
    protected PageElement voteDownButton;

    @Nonnull
    public TimedCondition isAt()
    {
        return voteUpButton.timed().isPresent();
    }

    @Nonnull
    public TimedCondition hasVoteDown()
    {
        return voteDownButton.timed().isPresent();
    }

    @Override
    public String getUrl() {
        return URI;
    }
}
