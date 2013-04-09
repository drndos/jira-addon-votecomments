package ut.info.renjithv.votecomments;

import org.junit.Test;
import info.renjithv.votecomments.MyPluginComponent;
import info.renjithv.votecomments.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}