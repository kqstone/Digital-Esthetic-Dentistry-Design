package tk.kqstone.dedd.ui;

import tk.kqstone.dedd.ui.TabPanel.TabLabel;

public class TabEvent {
	private TabLabel tab;

	public TabLabel getTab() {
		return tab;
	}

	public String getTabLabel() {
		return tab.getText();
	}

	public void setTab(TabLabel tab) {
		this.tab = tab;
	}

	public TabEvent(TabLabel tab) {
		this.tab = tab;
	}

}
