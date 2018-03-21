package com.knightlore.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.knightlore.render.PixelBuffer;
import com.knightlore.render.font.Font;

public class Table extends GUIObject{
	 
	private HashMap<String,Double> headersAndWidth;
	//...
	private CopyOnWriteArrayList<CopyOnWriteArrayList<String>> entries;
	
	public Table(int x, int y, int width, int height, int depth) {
	        super(x, y, width, height, depth);
	        this.headersAndWidth = new LinkedHashMap<>();
	        this.entries = new CopyOnWriteArrayList<>();
	 }
	
	public void setTableHeader(ArrayList<String> headers){
		int maxNoOfChars=0;
		for(String t : headers)
			maxNoOfChars += t.toCharArray().length;
		for(String t : headers){
			this.headersAndWidth.put(t, (double)((double)t.toCharArray().length/(double)maxNoOfChars));
		}
	}
	
	public void addTableEntry(CopyOnWriteArrayList<String> entry){
		synchronized (this.entries) {
			for(CopyOnWriteArrayList<String> elem : this.entries){
				if(elem.get(0).equals(entry.get(0))){
					this.entries.remove(elem);
					this.entries.add(entry);
					return;
				}
			}
			this.entries.add(entry);
		}
	}
	
	public void removeTableEntry(String uuid) {
		synchronized (this.entries) {
			CopyOnWriteArrayList<String> toBeRemoved = null;
			for (CopyOnWriteArrayList<String> entry : this.entries) {
				if (entry.get(0).equals(uuid)) {
					toBeRemoved = entry;
					break;
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
	void Draw(PixelBuffer pix, int x, int y) {
		synchronized (this.entries) {
		    int color = Color.DARK_GRAY.getRGB();
			//g.setFont(new java.awt.Font("Bookman Old Style Bold", 10, 15));
			int totalHeight = 0;
			totalHeight = (Font.DEFAULT_WHITE.getHeight() + 5) * (1 + this.entries.size());
			pix.fillRect(color, rect.x, rect.y, rect.width, totalHeight);
			color = Color.WHITE.getRGB();
			int lastX = rect.x;
			int lastY = rect.y;

			ArrayList<Double> entriesWidth = new ArrayList<>();

			// render the header
			for (String t : this.headersAndWidth.keySet()) {
				// draw cell
				pix.drawRect(color, lastX, lastY, (int) (this.rect.width * this.headersAndWidth.get(t)),
						totalHeight + 5);
				// draw text
				int hOffset = Font.DEFAULT_WHITE.getHeight();
				// draw the characters of the string
				pix.drawString(Font.DEFAULT_WHITE, t, lastX, lastY+hOffset, 15, 2);
				lastX += this.rect.width * this.headersAndWidth.get(t);
				entriesWidth.add(this.headersAndWidth.get(t));
			}
			lastY += Font.DEFAULT_WHITE.getHeight() + 5;
			lastX = rect.x;
			
			this.orderTableByScore();
			
			// render the entries
			for (CopyOnWriteArrayList<String> wholeEntry : this.entries) {
				int i = -1;
				for (String entry : wholeEntry) {
					if(i<0){
						i++;
						continue;
					}
					// draw cell
					pix.drawRect(color, lastX, lastY, (int) (this.rect.width * entriesWidth.get(i)),
							Font.DEFAULT_WHITE.getHeight() + 5);
					// draw text
					int hOffset = Font.DEFAULT_WHITE.getHeight();
					// draw the characters of the string
					pix.drawString(Font.DEFAULT_WHITE, entry, lastX, lastY+hOffset, 15, 2);
					lastX += this.rect.width * entriesWidth.get(i);
					i++;
				}
				lastY += Font.DEFAULT_WHITE.getHeight() + 5;
				lastX = rect.x;
			}
		}
	}
	
}
