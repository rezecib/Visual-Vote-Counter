package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;

/**	manages a single-selection list of objects of type T in a scrolling panel
 *	@author rezecib
 *	@param <T> the type of object to be displayed in this scroll lister			*/
public class ScrollLister<T> extends JScrollPane
{
	private static final long serialVersionUID = 1L;

	/**	the component that manages the display of the list	*/
	private JList<T> lister;
	
	/**	the model that manages the items in the list	*/
	private DefaultListModel<T> list;
	
	/**	the set of items to be displayed in the list	*/
	private Set<T> elements;
	
	/**	creates a new scrolling list
	 *	@param listener the parent that implements <code>ListSelectionListener</code> to detect changes in selection	*/
	public ScrollLister(ListSelectionListener listener)
	{
		super();
		list = new DefaultListModel<T>();
		lister = new JList<T>(list);
		lister.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lister.addListSelectionListener(listener);
		add(lister);
		setViewportView(lister);
		
		elements = new TreeSet<T>();
	}
	
	/** @return the selected list item, or <code>null</code> if nothing is selected	*/
	public T getSelected()
	{
		if(lister.getSelectedValue() != null)
			return lister.getSelectedValue();
		else
			return null;
	}
	
	public int getSelectedIndex()
	{
		if(lister.getSelectedValue() != null)
			return lister.getSelectedIndex();
		else
			return -1;
	}
	
	/**	sets the selection to the provided list item, if present
	 *	@param item the item to being selected						*/
	public void setSelected(T item)
	{	lister.setSelectedValue(item, true);	}
	
	/**	filters the list to show elements beginning with the provided text
	 *	@param pattern the string that all elements in the displayed list should begin with	*/
	public void filter(String pattern)
	{
		list.clear();
		pattern += ".*";
		for(T element : elements)
		{
			if(element.toString().matches(pattern))
				list.addElement(element);
		}
	}
	
	/**	sets the displayed list to the one provided
	 *	@param newList the new list to be displayed	*/
	public void setList(Set<T> newList)
	{
		elements = newList;
		list.clear();
		for(T element : newList)
		{
			boolean add = true;
			if(add)
				list.addElement(element);
		}
	}
	
	/**	sets the displayed list to the one provided
	 *	@param newList the new list to be displayed	*/
	public void addNew(Set<T> newList)
	{
		elements = newList;
		for(T element : newList)
		{
			if(!list.contains(element))
				list.addElement(element);
		}
	}
	
	public void add(T element)
	{
		list.addElement(element);
	}
	
	public void remove(T element)
	{
		list.removeElement(element);
	}
	
	/** @return The JList<T> used in the ScrollLister. Allows for custom visual styles.*/
	public JList<T> getList()
	{ return lister; }
	
	/**	Sets the background and foreground colors of the field and list	*/
	public void setColors(Color fore, Color back)
	{
		lister.setForeground(fore);
		lister.setBackground(back);
	}
	
	/** Sets the font used for the list */
	public void setListFont(Font font)
	{ lister.setFont(font); }
	
	/**
	 * Gets the element at index i in the list.
	 * @param i the index of the element to be retrieved.
	 * @return T element at index i.
	 */
	public T getElement(int i)
	{	return list.get(i);	}
	
	/**	a key has been pressed; detects arrow keys for selection purposes	*/
	public void sendKeyPress(KeyEvent e)
	{
		int code = e.getKeyCode();
		if(code == Key.DOWNARROW)
			lister.setSelectedIndex(lister.getSelectedIndex()+1);
		if(code == Key.UPARROW)
		{
			int index = lister.getSelectedIndex();
			if(index == 0)
				lister.clearSelection();
			else
				lister.setSelectedIndex(index-1);
		}
	}
}