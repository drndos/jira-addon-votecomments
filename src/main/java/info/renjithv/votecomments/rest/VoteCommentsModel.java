package info.renjithv.votecomments.rest;

import javax.xml.bind.annotation.*;
@XmlRootElement(name = "commentvotes")
@XmlAccessorType(XmlAccessType.FIELD)
public class VoteCommentsModel {

    @XmlElement(name = "commentid")
    private Long commentId;

    @XmlElement(name = "upvotes")
    private Integer upVotes;

    @XmlElement(name = "downvotes")
    private Integer downVotes;

    public VoteCommentsModel() {
    }

    public VoteCommentsModel(Long commentId, Integer upVotes, Integer downVotes) {
        this.commentId = commentId;
        this.upVotes = upVotes;
        this.downVotes = downVotes;
    }

    public Integer getDownVotes() {
        return downVotes;
    }

    public void setDownVotes(Integer downVotes) {
        this.downVotes = downVotes;
    }

    public Integer getUpVotes() {
        return upVotes;
    }

    public void setUpVotes(Integer upVotes) {
        this.upVotes = upVotes;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }
}