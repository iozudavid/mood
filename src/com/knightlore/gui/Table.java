package com.knightlore.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Table extends GUIObject{
	 
	private int columnNo;
	private HashMap<String,Double> headersAndWidth;
	//...
	private CopyOnWriteArrayList<CopyOnWriteArrayList<String>> entries;
	
	public Table(int x, int y, int width, int height, int depth, int columnNo) {
	        super(x, y, width, height, depth);
	        this.columnNo = columnNo;
	        this.headersAndWidth = new LinkedHashMap<>();
	        this.entries = new CopyOnWriteArrayList<>();
	 }
	
	public void setTableHeader(ArrayList<String> headers){
		if(headers.size()<this.columnNo)
			return;
		int maxNoOfChars=0;
		for(String t : headers)
			maxNoOfChars += t.toCharArray().length;
		for(String t : headers){
			this.headersAndWidth.put(t, (double)((double)t.toCharArray().length/(double)maxNoOfChars));
		}
	}
	
	public void addTableEntry(CopyOnWriteArrayList<String> entry){
		synchronized (this.entries) {
			if (entry.size() < this.columnNo)
				return;
			this.entries.add(entry);
		}
	}
	
	public void removeTableEntry(String entityName){
		synchronized (this.entries) {
			CopyOnWriteArrayList<String> toBeRemoved = null;
			for (CopyOnWriteArrayList<String> entry : this.entries) {
				for (String data : entry) {
					if (data.equals(entityName)) {
						toBeRemoved = entry;
						break;
					}
				}
				if (toBeRemoved != null)
					break;
			}
			if (toBeRemoved != null)
				this.entries.remove(toBeRemoved);
		}
	}
	
	private void orderTableByScore() {
		if (this.entries.size() < 2)
			return;
		try {
			this.entries.sort(new Comparator<CopyOnWriteArrayList<String>>() {

				@Override
				public int compare(CopyOnWriteArrayList<String> o1, CopyOnWriteArrayList<String> o2) {
					int score1 = Integer.parseInt(o1.get(o1.size() - 1));
					int score2 = Integer.parseInt(o2.get(o2.size() - 1));
					return score1 > score2 ? -1 : 1;
				}
			});
		} catch (NumberFormatException e) {
			return;
		}
	}

	@Override
	void Draw(Graphics g, Rectangle parentRect) {
		synchronized (this.entries) {
			g.setColor(Color.DARK_GRAY);
			g.setFont(new java.awt.Font("Bookman Old Style Bold", 10, 15));
			int totalHeight = 0;
			totalHeight = (g.getFontMetrics().getHeight() + 5) * (1 + this.entries.size());
			g.fillRect(rect.x, rect.y, rect.width, totalHeight);
			g.setColor(Color.WHITE);
			int lastX = rect.x;
			int lastY = rect.y;

			ArrayList<Double> entriesWidth = new ArrayList<>();

			// render the header
			for (String t : this.headersAndWidth.keySet()) {
				// draw cell
				g.drawRect(lastX, lastY, (int) (this.rect.width * this.headersAndWidth.get(t)),
						g.getFontMetrics().getHeight() + 5);
				// draw text
				int hOffset = g.getFontMetrics().getHeight();
				// draw the characters of the string
				g.drawChars(t.toCharArray(), 0, t.toCharArray().length, lastX, lastY + hOffset);
				lastX += this.rect.width * this.headersAndWidth.get(t);
				entriesWidth.add(this.headersAndWidth.get(t));
			}
			lastY += g.getFontMetrics().getHeight() + 5;
			lastX = rect.x;
			
			this.orderTableByScore();
			
			// render the entries
			for (CopyOnWriteArrayList<String> wholeEntry : this.entries) {
				int i = 0;
				for (String entry : wholeEntry) {
					// draw cell
					g.drawRect(lastX, lastY, (int) (this.rect.width * entriesWidth.get(i)),
							g.getFontMetrics().getHeight() + 5);
					// draw text
					int hOffset = g.getFontMetrics().getHeight();
					// draw the characters of the string
					g.drawChars(entry.toCharArray(), 0, entry.toCharArray().length, lastX, lastY + hOffset);
					lastX += this.rect.width * entriesWidth.get(i);
					i++;
				}
				lastY += g.getFontMetrics().getHeight() + 5;
				lastX = rect.x;
			}
		}
	}
	
}
