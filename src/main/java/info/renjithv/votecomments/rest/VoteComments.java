package info.renjithv.votecomments.rest;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.security.Permissions;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.transaction.TransactionCallback;
import info.renjithv.votecomments.VoteInfo;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Hashtable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A resource of message.
 */
@Path("/")
public class VoteComments {

    private static final Logger log = LogManager.getLogger("votecomments");
    private ActiveObjects ao;
    private IssueManager issueManager;
    private PermissionManager permissionManager;
    private CommentManager commentManager;

    public VoteComments(ActiveObjects ao, IssueManager issueManager, PermissionManager permissionManager, CommentManager commentManager) {
        this.issueManager = issueManager;
        this.permissionManager = permissionManager;
        this.commentManager = commentManager;
        this.ao = checkNotNull(ao);
    }

    @GET
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON})
    @Path("commentsvotes")
    public Response getIssueCommentsVotes(@QueryParam("issueid") final Long issueid) {
        if (null == issueid) {
            return Response.notModified("Issue Id missing").build();
        }
        else {
            log.warn(issueid);
        }

        final User loggedInUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
        final Hashtable<Long, VoteCommentsModel> data = new Hashtable<Long, VoteCommentsModel>();
        final MutableIssue issueObject = issueManager.getIssueObject(issueid);

        if (null != issueObject && permissionManager.hasPermission(Permissions.VIEW_VOTERS_AND_WATCHERS, issueObject, loggedInUser)) {
            ao.executeInTransaction(new TransactionCallback<Void>() {
                @Override
                public Void doInTransaction() {
                    VoteInfo[] votes = ao.find(VoteInfo.class, "ISSUE_ID = ?", issueid);
                    for (VoteInfo voteInfo : votes) {
                        log.info("Vote id - " + voteInfo.getID());
                        VoteCommentsModel inData = new VoteCommentsModel(voteInfo.getCommentId(), 0, 0);
                        if (data.containsKey(voteInfo.getCommentId())) {
                            inData = data.get(voteInfo.getCommentId());
                        }
                        switch (voteInfo.getVoteCount()) {
                            case -1:
                                inData.setDownVotes(inData.getDownVotes() + 1);
                                break;
                            case 1:
                                inData.setUpVotes(inData.getUpVotes() + 1);
                                break;
                            default:
                                log.error("No way this can happen");
                        }
                        data.put(voteInfo.getCommentId(), inData);
                    }
                    return null;
                }
            });
        }
        else
        {
            log.warn("Get votes request ignored");
        }
        return Response.ok(data.values()).build();
    }

    @GET
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("upvote")
    public Response upvoteComment(@QueryParam("commentid") final Long commentid, @QueryParam("issueid") final Long issueid) {
        if (null == issueid || null == commentid) {
            return Response.notModified("Required parameters missing").build();
        }
        UpdateVote(1, commentid, issueid);
        return Response.ok(new VoteCommentsModel(commentid, 0, 0)).build();
    }

    @GET
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("downvote")
    public Response downvoteComment(@QueryParam("commentid") Long commentid, @QueryParam("issueid") final Long issueid) {
        if (null == issueid || null == commentid) {
            return Response.notModified("Required parameters missing").build();
        }
        UpdateVote(-1, commentid, issueid);
        return Response.ok(new VoteCommentsModel(commentid, 0, 0)).build();
    }

    private void UpdateVote(final Integer increment, final Long commentid, final Long issueid) {
        final User loggedInUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
        final MutableIssue issueObject = issueManager.getIssueObject(issueid);
        final Comment comment = commentManager.getCommentById(commentid);

        if (null != issueObject &&
                null != loggedInUser &&
                permissionManager.hasPermission(Permissions.VIEW_VOTERS_AND_WATCHERS, issueObject, loggedInUser) &&
                null != comment &&
                !comment.getAuthorUser().equals(loggedInUser)) {

            ao.executeInTransaction(new TransactionCallback<Void>() {
                @Override
                public Void doInTransaction() {
                    VoteInfo[] votes = ao.find(VoteInfo.class, "COMMENT_ID = ? AND USER_NAME = ? AND ISSUE_ID = ?",
                            commentid, loggedInUser.getName(), issueid);
                    switch (votes.length) {
                        case 0:
                            final VoteInfo voteInfo = ao.create(VoteInfo.class);
                            voteInfo.setCommentId(commentid);
                            voteInfo.setIssueId(issueid);
                            voteInfo.setUserName(loggedInUser.getName());
                            voteInfo.setVoteCount(increment);
                            voteInfo.save();
                            break;
                        case 1:
                            log.info("Existing vote found for this user, comment and issue");
                            Integer vote = votes[0].getVoteCount();
                            vote = vote + increment;
                        /*
                        * -1 + 1  = 0 = delete
                        * 0  + 1  => This is not possible
                        * 1  + 1  => 2 = 1
                        * -1 + -1 => -2 = -1
                        * 0  + -1 => This is not possible
                        * 1  + -1 = 0 = delete
                        * */
                            switch (vote) {
                                case 0:
                                    ao.delete(votes[0]);
                                    break;
                                case 2:
                                    vote = 1;
                                    votes[0].setVoteCount(vote);
                                    votes[0].save();
                                    break;
                                case -2:
                                    vote = -1;
                                    votes[0].setVoteCount(vote);
                                    votes[0].save();
                                    break;
                                default:
                                    log.warn("This case should never come for vote count");
                                    break;
                            }
                            break;
                        default:
                            log.error("More that one vote found for the same comment from same user, this should never happen");
                    }
                    return null;
                }
            });
        } else {
            log.warn("Update vote request ignored");
        }
    }
}