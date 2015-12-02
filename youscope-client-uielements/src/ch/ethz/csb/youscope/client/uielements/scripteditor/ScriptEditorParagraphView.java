/**
 * 
 */
package ch.ethz.csb.youscope.client.uielements.scripteditor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.text.Element;
import javax.swing.text.ParagraphView;
import javax.swing.text.View;

/**
 * @author langmo
 *
 */
class ScriptEditorParagraphView extends ParagraphView
{
	private static final short NUMBERS_WIDTH = 25;

    public ScriptEditorParagraphView(Element e) 
    {
        super(e);
        short top = 0;
        short left = 0;
        short bottom = 0;
        short right = 0;
        this.setInsets(top, left, bottom, right);
    }

    @Override
    protected void setInsets(short top, short left, short bottom, short right) 
    {
    	
    	super.setInsets(top,(short)(left + NUMBERS_WIDTH + 2), bottom,right);
    }

    @Override
    public void paintChild(Graphics g, Rectangle r, int n) 
    {
        super.paintChild(g, r, n);
        int previousLineCount = getPreviousLineCount();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(r.x-getLeftInset(), r.y, NUMBERS_WIDTH, r.height);
        g.setColor(Color.DARK_GRAY);
        String text = Integer.toString(previousLineCount + n + 1);
        int stringWidth = g.getFontMetrics().stringWidth(text);
        g.drawString(text, r.x - getLeftInset() + NUMBERS_WIDTH - stringWidth - 1, r.y + r.height - 5);
    }

    public int getPreviousLineCount()
    {
        int lineCount = 0;
        View parent = this.getParent();
        int count = parent.getViewCount();
        for (int i = 0; i < count; i++) 
        {
            if (parent.getView(i) == this) 
            {
                break;
            }
			lineCount += parent.getView(i).getViewCount();
        }
        return lineCount;
    }
}
