package pt.inescid.gsd.guimin.common;


import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.Component;
import java.awt.Container;
import java.awt.TextComponent;
import java.awt.Window;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListModel;
import javax.swing.plaf.basic.BasicComboPopup;


import net.javaprog.ui.wizard.Step;
import pt.inescid.gsd.guimin.common.model.log.GaudiJTextField;


public class GaudiGuiHierarchy {

	private HashMap<String,Component> registerK2W;
	private HashMap<Component,String> registerW2K;


	//jmatos: aparentely some buttons, namely togglebuttons, change after the registration
	private HashMap<String,Component> name2button = new HashMap<String, Component>();



	//jmatos
	public ArrayList<GaudiJTextField> textfields = new ArrayList<GaudiJTextField>();
	public ArrayList<Component> widgetsV = new ArrayList<Component>();



	public HashMap<String, Component> getRegisterK2W() {
		return registerK2W;
	}

	public void setRegisterK2W(HashMap<String, Component> registerK2W) {
		this.registerK2W = registerK2W;
	}

	public HashMap<Component, String> getRegisterW2K() {
		return registerW2K;
	}

	public void setRegisterW2K(HashMap<Component, String> registerW2K) {
		this.registerW2K = registerW2K;
	}

	public GaudiGuiHierarchy(){
		this.registerK2W = new HashMap<String,Component>();
		this.registerW2K = new HashMap<Component,String>();
	}

	public void processGUI(){
		Component[] rootWindows = Window.getWindows();
		System.err.println("fdx:::::: "+rootWindows.length);
		this.processWidgetChildren(rootWindows, "0");
		
		for(Component c: rootWindows)
			genStuff(c, "0",true);///remove this line
	}



	FileWriter fw=null;
	int i=0;
	public void genStuff(Component w, String index,boolean init) {
		if(init)
			writeInit(w.getX(),w.getY(),w.getHeight(),w.getWidth(),index);
		if(w instanceof JMenu){
			JMenu c = (JMenu)w;
			genStuff3(c.getMenuComponents().length);
			for(int i=0;i<c.getMenuComponents().length;i++)
				genStuff2(w, c.getMenuComponents()[i],i,index);
			for(int i=0;i<c.getMenuComponents().length;i++)
				genStuff(c.getMenuComponents()[i], index, false);
		}
		else if(w instanceof Container){
			Container c = (Container)w;
			genStuff3(c.getComponents().length);
			for(int i=0;i<c.getComponents().length;i++)
				genStuff2(w, c.getComponents()[i],i,index);
			for(int i=0;i<c.getComponents().length;i++)
				genStuff(c.getComponents()[i], index, false);
		}
		


	}
	public void genStuff3(int size) {
		try{
			if(fw==null)
				fw = new FileWriter("/Users/jmatos/Desktop/genCode.txt");
			fw.write("c"+(i-1)+".component=new Component["+size+"];\n");
			fw.flush();
		}
		catch(Exception e) {e.printStackTrace();}
	}
	public void genStuff2(Component parent, Component child,int arrayIndex,String index) {
		writeInit2(child.getX(),child.getY(),child.getHeight(),child.getWidth(),index);
		try{
			if(fw==null)
				fw = new FileWriter("/Users/jmatos/Desktop/genCode.txt");
			fw.write("c"+(i-(arrayIndex+2))+".component["+arrayIndex+"]=c"+(i-1)+";\n");
			fw.flush();
		}
		catch(Exception e) {e.printStackTrace();}
		
	}
	public void writeInit(int x,int y, int h, int w, String id) {
		try{
			if(fw==null)
				fw = new FileWriter("/Users/jmatos/Desktop/genCode.txt");
			fw.write("Component c"+i+" = new Component("+x+","+y+","+h+","+w+",\""+id+"\");\n");
			fw.flush();
			i++;
		}
		catch(Exception e) {e.printStackTrace();}
	}
	public void writeInit2(int x,int y, int h, int w, String id) {
		try{
			if(fw==null)
				fw = new FileWriter("/Users/jmatos/Desktop/genCode.txt");
			fw.write("Container c"+i+" = new Container("+x+","+y+","+h+","+w+",\""+id+"\");\n");
			fw.flush();
			i++;
		}
		catch(Exception e) {e.printStackTrace();}
	}

	public void processWidget(Component w, String index){
		//genStuff(w, index,true);///remove this line
		if(this.registerWidget(w, index)){		


			if(w instanceof Container){
				Container c = (Container)w;
				this.processWidgetChildren(c.getComponents(), index);
			}
			if(w instanceof JMenu){
				JMenu c = (JMenu)w;
				this.processWidgetChildren(c.getMenuComponents(), index);
			}


			if(w instanceof JComponent) {

				JComponent jc = (JComponent) w;
				JPopupMenu jpm = jc.getComponentPopupMenu();
				if(jpm!=null)
					this.processWidgetChildren(jpm.getComponents(),index);
			}

			if(w instanceof BasicComboPopup) {
				BasicComboPopup bcp = (BasicComboPopup) w;
				this.processWidget(bcp.getList(), index);

				ListModel lm = bcp.getList().getModel();
				for(int i=0;i<lm.getSize(); i++){
					Object elem = lm.getElementAt(i);
					if(elem instanceof Component)
						this.processWidget((Component)elem, index);
				}

			}

			boolean value=false;

			if(w instanceof JTextField){

				GaudiJTextField gjtf = new GaudiJTextField(index, (JTextField)w);
				this.textfields.add(gjtf);
				value = true;
			}

			if(w instanceof Checkbox){
				this.widgetsV.add(w);
				value = true;
			}

			if(w instanceof Canvas){
				this.widgetsV.add(w);
				value = true;
			}

			if(w instanceof TextComponent){
				this.widgetsV.add(w);
				value = true;
			}

			if(w instanceof JCheckBox){
				this.widgetsV.add(w);
				value = true;
			}

			if(w instanceof JRadioButton){
				this.widgetsV.add(w);
				value = true;
			}

			if(w instanceof JTextArea){
				this.widgetsV.add(w);
				value = true;
			}



			/*			if(w instanceof JToolBar){
				JToolBar jtl = (JToolBar)w;
				JPopupMenu jpm = jtl.getComponentPopupMenu();
				if(jpm!=null)
					this.processWidgetChildren(jpm.getComponents(),index);

				this.processWidgetChildren(jtl.getComponents(), index);

			}*/



			/*			if(w instanceof JRootPane) {

				JRootPane jrp = (JRootPane) w;

				Component [] components = new Component[4];
				components[0] = jrp.getContentPane();
				components[1] = jrp.getGlassPane();
				components[2] = jrp.getJMenuBar();
				components[3] = jrp.getLayeredPane();

				this.processWidgetChildren(components, index);

			}*/

			/*		if(w instanceof JMenuBar) {

				JMenuBar menu = (JMenuBar) w;
				Component [] components = new Component[menu.getMenuCount()];

				for(int i=0; i<menu.getMenuCount();i++)
					components[i] = menu.getMenu(i);
				this.processWidgetChildren(components, index);

				MenuElement [] elements = menu.getSubElements();
				components = new Component[elements.length];
				for(int i=0;i<elements.length;i++)
					components[i] = elements[i].getComponent();

				this.processWidgetChildren(components, index);
			}
			 */

			/*		if(w instanceof JDialog) {
				JDialog dialog = (JDialog)w;
				Component [] components = new Component[5];
				components[0] = dialog.getGlassPane();
				components[1] = dialog.getJMenuBar();
				components[2] = dialog.getContentPane();
				components[3] = dialog.getLayeredPane();
				components[4] = dialog.getRootPane();

				this.processWidgetChildren(components, index);

			}*/

			if(w instanceof JPanel) {



			}

			if(w instanceof Window) {


			}



			if(w instanceof JToggleButton) {

				JToggleButton jtb = (JToggleButton)w;
				String name = jtb.getName();
				if(name!=null && !name2button.containsKey(name))
					this.name2button.put(name, w);

			}

			if(w instanceof JMenuItem) {

				JMenuItem jmi = (JMenuItem) w;
				String text = jmi.getText();
				if(text!=null && !name2button.containsKey(text))
					this.name2button.put(text, w);

			}













			/////jmatos: these are for columba
			if(w instanceof net.javaprog.ui.wizard.Wizard) {//jmatos
				net.javaprog.ui.wizard.Wizard wizzard = (net.javaprog.ui.wizard.Wizard) w;
				Component [] components = wizzard.getComponents();
				if(components!=null && components.length>0)
					this.processWidgetChildren(components, index);


				Step [] steps = wizzard.getModel().getSteps();
				components = new Component[steps.length];
				for(int i=0; i<steps.length; i++)
					components[i]=steps[i].getComponent();

				this.processWidgetChildren(components, index);

			}










			///TODO: we dont need this

			if(w instanceof net.javaprog.ui.wizard.plaf.basic.BasicWizardNavigator) {//jmatos
				net.javaprog.ui.wizard.plaf.basic.BasicWizardNavigator nav= (net.javaprog.ui.wizard.plaf.basic.BasicWizardNavigator) w;
				//nav.updateNavigation();
				//nav.updateUI();

				JPopupMenu jpm = nav.getComponentPopupMenu();
				if(jpm!=null)
					this.processWidgetChildren(jpm.getComponents(), index);

				Component [] components = nav.getComponents();
				if(components!=null && components.length>0)
					this.processWidgetChildren(components, index);

			}
			if(w instanceof net.javaprog.ui.wizard.WizardContentPane) {//jmatos

				net.javaprog.ui.wizard.WizardContentPane pane= (net.javaprog.ui.wizard.WizardContentPane) w;
				JPopupMenu jpm = pane.getComponentPopupMenu();
				if(jpm!=null)
					this.processWidgetChildren(jpm.getComponents(), index);

				Component [] components = pane.getComponents();
				if(components!=null && components.length>0)
					this.processWidgetChildren(components, index);
			}
			if(w instanceof net.javaprog.ui.wizard.plaf.windows.WindowsStepDescriptionRenderer.UIResource) {//jmatos
				net.javaprog.ui.wizard.plaf.windows.WindowsStepDescriptionRenderer.UIResource uir= (net.javaprog.ui.wizard.plaf.windows.WindowsStepDescriptionRenderer.UIResource) w;
				JPopupMenu jpm = uir.getComponentPopupMenu();
				if(jpm!=null)
					this.processWidgetChildren(jpm.getComponents(), index);

				Component [] components = uir.getComponents();
				if(components!=null && components.length>0)
					this.processWidgetChildren(components, index);

			}
//			if(w instanceof org.columba.core.gui.base.WizardTextField){
//				org.columba.core.gui.base.WizardTextField wtf = (org.columba.core.gui.base.WizardTextField)w;
//				this.processWidgetChildren(wtf.getComponents(),index);
//
//			}












		}
	}



	public void processWidgetChildren(Component[] array, String pindex){

		if(array == null)
			return;

		int index = 0;

		for(Component w : array){
			this.processWidget(w, pindex + "/" + String.valueOf(index));
			index++;
		}
	}


	public boolean registerWidget(Component w, String s){

		if(w == null)
			return false;

		if(this.registerK2W.get(s) != null)
			return true;

//if(w.toString().contains("New"))
		System.err.println("[Gaudi]-->Registering: " + w.getClass().getCanonicalName() + " with id: " + s+
				"; coordinates: ("+w.getX()+","+w.getY()+"); (h/w): ("+w.getHeight()+","+w.getWidth()+
				") ; container: "+(w instanceof Container)+" :: "+w.getName());
		this.registerK2W.put(s,w);
		this.registerW2K.put(w,s);
		return true;
	}





	public void updateWidget(Component w, String index) {

		System.out.println("[Gaudi]-->Updating: " + w.getClass().getCanonicalName() + " with id: " + index);
		this.registerW2K.remove(index);
		this.registerK2W.put(index,w);
		this.registerW2K.put(w,index);

		if(w instanceof Container){
			Container c = (Container)w;
			this.updateWidgetChildren(c.getComponents(), index);
		}
		if(w instanceof JMenu){
			JMenu c = (JMenu)w;
			this.updateWidgetChildren(c.getMenuComponents(), index);
		}

	}

	public void updateWidgetChildren(Component[] array, String pindex){

		if(array == null)
			return;

		int index = 0;

		for(Component w : array){
			this.updateWidget(w, pindex + "/" + String.valueOf(index));
			index++;
		}
	}

	public String findWidgetId(Component w){

		//jmatos: sometimes jtogglebuttons mutate after the registration
		if(w instanceof JToggleButton){
			String name = ((JToggleButton)w).getName();
			if(name!=null){
				Component x = this.name2button.get(name);
				return this.registerW2K.get(x);
			}
		}
		else if(w instanceof JMenuItem) {
			String text = ((JMenuItem)w).getText();
			if(text!=null){
				Component x = this.name2button.get(text);
				return this.registerW2K.get(x);
			}
		}

		return this.registerW2K.get(w);
		////////


		//String id = this.registerW2K.get(w);
		//return id;
	}

	public Component findWidget(String id){
		Component w = this.registerK2W.get(id);
		return w;
	}

	public void printHierarchy() {


		try{

			FileWriter fw = new FileWriter(System.getProperty("user.home")+"/Desktop/Hierarchy.txt");

			for(String s: registerK2W.keySet())
				fw.write(s+" ---> "+registerK2W.get(s).getClass().toString()+" \n");

			fw.flush();
			fw.close();
		}
		catch(Exception e) {}
	}


}
