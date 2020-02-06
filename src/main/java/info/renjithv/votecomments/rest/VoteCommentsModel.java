package info.renjithv.votecomments.rest;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "commentvotes")
@XmlAccessorType(XmlAccessType.FIELD)
public class VoteCommentsModel {

    @XmlElement(name = "commentid")
    private Long commentId;

    @XmlElement(name = "upvotes")
    private Integer upVotes;

    @XmlElement(name = "downvotes")
    private Integer downVotes;

    @XmlElement(name = "likers")
    private List<String> likers;

    @XmlElement(name = "dislikers")
    private List<String> dislikers;

    public VoteCommentsModel() {
    }

    public VoteCommentsModel(Long commentId, Integer upVotes, Integer downVotes, List<String> likers, List<String> dislikers) {
        this.commentId = commentId;
        this.upVotes = upVotes;
        this.downVotes = downVotes;
        this.likers = likers;
        this.dislikers = dislikers;
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

    public List<String> getLikers() {
        return likers;
    }

    public void setLikers(List<String> likers) {
        this.likers = likers;
    }

    public List<String> getDislikers() {
        return dislikers;
    }

    public void setDislikers(List<String> dislikers) {
        this.dislikers = dislikers;
    }
}