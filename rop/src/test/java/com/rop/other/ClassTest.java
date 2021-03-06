/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-6-6
 */
package com.rop.other;

import com.rop.AbstractRopRequest;
import com.rop.RopRequest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class ClassTest {

    @Test
    public void testAssignableFrom() {
        assertTrue(!MyRopRequest.class.isAssignableFrom(RopRequest.class));
        assertTrue(!MyRopRequest.class.isAssignableFrom(AbstractRopRequest.class));
        assertTrue(AbstractRopRequest.class.isAssignableFrom(MyRopRequest.class));
    }

    private class MyRopRequest extends AbstractRopRequest {

    }

}

