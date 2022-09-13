package org.vishia.gral.test.swt;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**Copyright / source: https://github.com/eclipse-platform/eclipse.platform.swt/blob/master/examples/org.eclipse.swt.snippets/src/org/eclipse/swt/snippets/Snippet76.java
 * https://www.eclipse.org/swt/snippets/
 * @author modified and commented by Hartmut Schorrig
 *
 */
public class SwtTestTabFolder {

  Rectangle shellBounds, tabFolderBounds;
  Rectangle[] tabContentBounds = new Rectangle[3];
  

  public static void main (String [] args) {
    SwtTestTabFolder thiz = new SwtTestTabFolder();
    Display display = new Display ();
    final Shell shell = new Shell (display);               // Display and Shell presents the Window
    int x=500, y=200, width=1200, height=600;
    //shell.setBounds(x, y, width, height);                  // determine the initial position on the screen and the size of the Window
    shell.setSize(width, height);                          // alternatively only the size with a default position
    thiz.shellBounds = shell.getBounds();
    System.out.println("size Window x * y = " + width + " * " + height);
    shell.setText("Snippet 76");                           // Title of the window
    final TabFolder tabFolder = new TabFolder (shell, SWT.BORDER);
    Rectangle areaInWindow = shell.getClientArea ();         // The area in pixel without Title and borders inside the window
    System.out.printf("pos x, y: size x * y in Window= %d, %d: %d * %d\n", areaInWindow.x, areaInWindow.y, areaInWindow.width, areaInWindow.height );
    //    tabFolder.setLocation (clientArea.x, clientArea.y);
//    tabFolder.setSize(clientArea.width,clientArea.height);
    tabFolder.setBounds(areaInWindow);                     // The tab folder should fill the whole area. Without the setBounds the TabFolder is not visible.
    thiz.tabFolderBounds = tabFolder.getBounds();
    Font fontTab = new Font(display, "Arial", 10, SWT.ITALIC);
    tabFolder.setFont(fontTab);
    Rectangle areaTabFolder1 = tabFolder.getClientArea ();  // position inside the areaWindow, size inclusively the tabs itself
    int yTab =-1;
    for (int i=0; i<3; i++) {
      Rectangle areaTabFolder = tabFolder.getClientArea ();  // position inside the areaWindow, size inclusively the tabs itself
      System.out.printf("pos x, y: size x * y Tabfolder= %d, %d: %d * %d\n", areaTabFolder.x, areaTabFolder.y, areaTabFolder.width, areaTabFolder.height );
      TabItem tabItem = new TabItem (tabFolder, SWT.NONE);
      //Rectangle areaTab = tabItem.getClientArea ();
      //item.setLocation (clientAreaTab.x, clientAreaTab.y);
      tabItem.setText ("tab " + i);
      //
      Composite tabComp = new Composite(tabFolder,0);
      tabItem.setControl(tabComp);
      //tabComp.setBounds(areaTabFolder);
      Rectangle areaTab;
      if(yTab == -1) {
        areaTab = tabComp.getClientArea ();
        yTab = areaTabFolder.height - areaTab.height;
      } else {
        tabComp.setBounds(0,yTab, areaTabFolder1.width, areaTabFolder1.height - yTab );
        areaTab = tabComp.getClientArea ();
      }
      thiz.tabContentBounds[i] = tabComp.getBounds();
      
      System.out.printf("pos x, y: size x * y Tab = %d, %d: %d * %d\n", areaTab.x, areaTab.y, areaTab.width, areaTab.height );
      Button button = new Button (tabComp, SWT.PUSH);
      //button.setBounds(10,50,100,200);
      int xButton = 10, yButton=30, widthButton=100, heightButton=50;
      button.setBounds(xButton, yButton, widthButton, heightButton);
      System.out.printf("pos x, y: size x * y Button = %d, %d: %d * %d\n", xButton, yButton, widthButton, heightButton );
      button.setText ("Page " + i);
    }
    //tabFolder.pack ();
    //shell.pack ();
    shell.open ();
    while (!shell.isDisposed ()) {
      if (!display.readAndDispatch ()) display.sleep ();
    }
    display.dispose ();
  }
}
