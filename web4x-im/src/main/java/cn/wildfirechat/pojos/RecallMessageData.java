/*
 * This file is part of the Wildfire Chat package.
 * (c) Heavyrain2012 <heavyrain.lee@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package cn.wildfirechat.pojos;

public class RecallMessageData {
    private String operator;
    private long messageUid;
    private boolean isUserRecall;

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public long getMessageUid() {
        return messageUid;
    }

    public void setMessageUid(long messageUid) {
        this.messageUid = messageUid;
    }

    public boolean isUserRecall() {
        return isUserRecall;
    }

    public void setUserRecall(boolean userRecall) {
        isUserRecall = userRecall;
    }
}
