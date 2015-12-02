/**
 * 
 */
package ch.ethz.csb.youscope.client.uielements.scripteditor;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JEditorPane;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * @author langmo
 *
 */
class ScriptEditorPane extends JEditorPane
{
	private static final long	serialVersionUID	= 4499543166341220249L;
	@Override
	public boolean getScrollableTracksViewportWidth()
    {
       return false ;
    }
	@Override
    public void setSize(Dimension d)
    {
        if (d.width < getParent().getSize().width)
        {
           d.width = getParent().getSize().width;
        }
        super.setSize(d);
    }
	@Override
	public Dimension getMaximumSize()
	{
		return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}
	
	public void setErrorInLine(int line)
	{
		try
		{
			Element lineElement = getDocument().getRootElements()[0].getElement(line-1);
			setCaretPosition(lineElement.getStartOffset());
			if(getDocument() instanceof ScriptEditorDocument)
			{
				SimpleAttributeSet errorLineAttributes = new SimpleAttributeSet();
				StyleConstants.setBackground(errorLineAttributes, Color.RED);
				((ScriptEditorDocument)getDocument()).setCharacterAttributes(lineElement.getStartOffset(), lineElement.getEndOffset() - lineElement.getStartOffset(), errorLineAttributes, true);
			}
		}
		catch(@SuppressWarnings("unused") Exception e1)
		{
			// Do nothing, it's only a feature.
		}
	}
	@Override
	public void setEditorKit(EditorKit editorKit)
	{
		String text = getText();
		super.setEditorKit(editorKit);
		setText(text);
	}
}
