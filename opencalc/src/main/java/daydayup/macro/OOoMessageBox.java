package daydayup.macro;

import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.KeyEvent;
import com.sun.star.awt.MessageBoxButtons;
import com.sun.star.awt.MouseEvent;
import com.sun.star.awt.Rectangle;
import com.sun.star.awt.XMessageBox;
import com.sun.star.awt.XMessageBoxFactory;
import com.sun.star.awt.XWindow;
import com.sun.star.awt.XWindowPeer;
import com.sun.star.beans.PropertyValue;
import com.sun.star.comp.helper.BootstrapException;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XModel;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.script.provider.XScriptContext;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

import ooo.connector.BootstrapSocketConnector;

public class OOoMessageBox {

	  private static final String OOO_EXEC_FOLDER = "C:/Program Files/OpenOffice.org 2.3/program/";

	  private static final String TYPE_INFO_MESSAGE_BOX = "infobox"; // infobox always shows one OK button alone!
	  private static final String TYPE_WARN_MESSAGE_BOX = "warningbox";
	  private static final String TYPE_ERROR_MESSAGE_BOX = "errorbox";
	  private static final String TYPE_QUESTION_MESSAGE_BOX = "querybox";
	  private static final String TYPE_SIMPLE_MESSAGE_BOX = "messbox";

	  private static final int RESULT_CANCLE_ABORT = 0;
	  private static final int RESULT_OK = 1;
	  private static final int RESULT_YES = 2;
	  private static final int RESULT_NO = 3;
	  private static final int RESULT_RETRY = 4;
	  private static final int RESULT_IGNORE = 5;

	  /**
	   * Called from a toolbar.
	   */
	  public static void showMessageBoxExamples(XScriptContext xScriptContext, Short ignored) {
	   showMessageBoxExamples(xScriptContext);
	  }

	  /**
	   * Called from a button with an action.
	   */
	  public static void showMessageBoxExamples(XScriptContext xScriptContext, ActionEvent ignored) {
	   showMessageBoxExamples(xScriptContext);
	  }

	  /**
	   * Called from a button with a key.
	   */
	  public static void showMessageBoxExamples(XScriptContext xScriptContext, KeyEvent ignored) {
	   showMessageBoxExamples(xScriptContext);
	  }

	  /**
	   * Called from a button with the mouse.
	   */
	  public static void showMessageBoxExamples(XScriptContext xScriptContext, MouseEvent ignored) {
	   showMessageBoxExamples(xScriptContext);
	  }

	  /**
	   * Called from a menu or the "Run Macro..." menu.
	   */
	  public static void showMessageBoxExamples(XScriptContext xScriptContext) {
	   XModel document = xScriptContext.getDocument();
	   showMessageBoxExamples(document);
	  }

	  private static void showMessageBoxExamples(XModel document) {
	   if (document == null)
	     return;

	   String messageBoxTitle;
	   String message;
	   short result;

	   // Get the parent window and access to the window toolkit of the parent window
	   XWindow parentWindow = document.getCurrentController().getFrame().getContainerWindow();
	   XWindowPeer parentWindowPeer = (XWindowPeer) UnoRuntime.queryInterface(XWindowPeer.class, parentWindow);

	   // Show examples of different message boxes
	   messageBoxTitle = "Simple Message Box";
	   message = "A message in a SimpleMessageBox.";
	   result = showSimpleMessageBox(parentWindowPeer,messageBoxTitle,message);
	   showResult(parentWindowPeer,result);

	   messageBoxTitle = "Info Message Box";
	   message = "A message in an InfoMessageBox.";
	   result = showInfoMessageBox(parentWindowPeer,messageBoxTitle,message);
	   showResult(parentWindowPeer,result);

	   messageBoxTitle = "Warning Message Box";
	   message = "A message in a WarningMessageBox.";
	   result = showYesNoWarningMessageBox(parentWindowPeer,messageBoxTitle,message);
	   showResult(parentWindowPeer,result);

	   messageBoxTitle = "Another Warning Message Box";
	   message = "A message in another WarningMessageBox.";
	   result = showOkCancelWarningMessageBox(parentWindowPeer,messageBoxTitle,message);
	   showResult(parentWindowPeer,result);

	   messageBoxTitle = "Question Message Box";
	   message = "A message in a QuestionMessageBox.";
	   result = showQuestionMessageBox(parentWindowPeer,messageBoxTitle,message);
	   showResult(parentWindowPeer,result);

	   messageBoxTitle = "Error Message Box";
	   message = "A message in an ErrorMessageBox.";
	   result = showAbortRetryIgnoreErrorMessageBox(parentWindowPeer,messageBoxTitle,message);
	   showResult(parentWindowPeer,result);

	   messageBoxTitle = "Another Error Message Box";
	   message = "A message in another ErrorMessageBox.";
	   result = showRetryCancelErrorMessageBox(parentWindowPeer,messageBoxTitle,message);
	   showResult(parentWindowPeer,result);
	  }

	  private static void showResult(XWindowPeer parentWindowPeer,short result) {
	   if (parentWindowPeer == null)
	     return;

	   String button;
	   switch (result) {
	     case RESULT_CANCLE_ABORT:
	      button = "Cancel or Abort";
	      break;
	     case RESULT_OK:
	      button = "OK";
	      break;
	     case RESULT_YES:
	      button = "Yes";
	      break;
	     case RESULT_NO:
	      button = "No";
	      break;
	     case RESULT_RETRY:
	      button = "Retry";
	      break;
	     case RESULT_IGNORE:
	      button = "Ignore";
	      break;
	     default:
	      button = "Unknown";
	   }
	         
	   String messageBoxTitle = "Result";
	   String message = "Button selected: "+button;
	   showInfoMessageBox(parentWindowPeer,messageBoxTitle,message);
	  }

	  private static short showSimpleMessageBox(XWindowPeer parentWindowPeer,String messageBoxTitle,String message) {
	   if (parentWindowPeer == null || messageBoxTitle == null || message == null)
	     return 0;

	   return showMessageBox(parentWindowPeer,TYPE_SIMPLE_MESSAGE_BOX,MessageBoxButtons.BUTTONS_YES_NO+MessageBoxButtons.DEFAULT_BUTTON_YES,messageBoxTitle,message);
	  }

	  private static short showInfoMessageBox(XWindowPeer parentWindowPeer,String messageBoxTitle,String message) {
	   if (parentWindowPeer == null || messageBoxTitle == null || message == null)
	     return 0;

	   return showMessageBox(parentWindowPeer,TYPE_INFO_MESSAGE_BOX,MessageBoxButtons.BUTTONS_OK,messageBoxTitle,message);
	  }

	  private static short showYesNoWarningMessageBox(XWindowPeer parentWindowPeer,String messageBoxTitle,String message) {
	   if (parentWindowPeer == null || messageBoxTitle == null || message == null)
	     return 0;

	   return showMessageBox(parentWindowPeer,TYPE_WARN_MESSAGE_BOX,MessageBoxButtons.BUTTONS_YES_NO+MessageBoxButtons.DEFAULT_BUTTON_NO,messageBoxTitle,message);
	  }

	  private static short showOkCancelWarningMessageBox(XWindowPeer parentWindowPeer,String messageBoxTitle,String message) {
	   if (parentWindowPeer == null || messageBoxTitle == null || message == null)
	     return 0;

	   return showMessageBox(parentWindowPeer,TYPE_WARN_MESSAGE_BOX,MessageBoxButtons.BUTTONS_OK_CANCEL+MessageBoxButtons.DEFAULT_BUTTON_OK,messageBoxTitle,message);
	  }

	  private static short showQuestionMessageBox(XWindowPeer parentWindowPeer,String messageBoxTitle,String message) {
	   if (parentWindowPeer == null || messageBoxTitle == null || message == null)
	     return 0;

	   return showMessageBox(parentWindowPeer,TYPE_QUESTION_MESSAGE_BOX,MessageBoxButtons.BUTTONS_YES_NO_CANCEL+MessageBoxButtons.DEFAULT_BUTTON_YES,messageBoxTitle,message);
	  }

	  private static short showAbortRetryIgnoreErrorMessageBox(XWindowPeer parentWindowPeer,String messageBoxTitle,String message) {
	   if (parentWindowPeer == null || messageBoxTitle == null || message == null)
	     return 0;

	   return showMessageBox(parentWindowPeer,TYPE_ERROR_MESSAGE_BOX,MessageBoxButtons.BUTTONS_ABORT_IGNORE_RETRY+MessageBoxButtons.DEFAULT_BUTTON_RETRY,messageBoxTitle,message);
	  }

	  private static short showRetryCancelErrorMessageBox(XWindowPeer parentWindowPeer,String messageBoxTitle,String message) {
	   if (parentWindowPeer == null || messageBoxTitle == null || message == null)
	     return 0;

	   return showMessageBox(parentWindowPeer,TYPE_ERROR_MESSAGE_BOX,MessageBoxButtons.BUTTONS_RETRY_CANCEL+MessageBoxButtons.DEFAULT_BUTTON_CANCEL,messageBoxTitle,message);
	  }

	  private static short showMessageBox(XWindowPeer parentWindowPeer,String messageBoxType,int messageBoxButtons,String messageBoxTitle,String message) {
	   if (parentWindowPeer == null || messageBoxType == null || messageBoxTitle == null || message == null)
	     return 0;

	   // Initialize the message box factory
	   XMessageBoxFactory messageBoxFactory = (XMessageBoxFactory) UnoRuntime.queryInterface(XMessageBoxFactory.class,parentWindowPeer.getToolkit());

	   Rectangle messageBoxRectangle = new Rectangle();

	   XMessageBox box = null;//messageBoxFactory.createMessageBox(parentWindowPeer, messageBoxType, arg2, arg3, arg4)
			   				//.createMessageBox(parentWindowPeer, messageBoxRectangle, messageBoxType, messageBoxButtons, messageBoxTitle, message) ;
	   return box.execute();
	  }

	  public static void main(String[] args) {
	   // This example works with the following arguments:
	   // 1) A valid name of an OOo document like "file:///c:/temp/writer.odt"
	   // 2) "private:factory/swriter" or "private:factory/scalc" to create a new OOo document
	   // 3) No argument at all to get the current OOo document
	   String loadUrl = (args.length > 0)? args[0]: null;
	   try {
	     XComponentContext context = BootstrapSocketConnector.bootstrap(OOO_EXEC_FOLDER);
	      
	     XMultiComponentFactory multiComponentFactory = context.getServiceManager();
	     Object desktopFrame = multiComponentFactory.createInstanceWithContext("com.sun.star.frame.Desktop", context);
	     XComponentLoader componentloader = (XComponentLoader) UnoRuntime.queryInterface(XComponentLoader.class,desktopFrame);

	     Object component;
	     if (loadUrl != null) {
	      // Load the document
	      try {
	        component = componentloader.loadComponentFromURL(loadUrl, "_blank", 0, new PropertyValue[0]);
	      } catch (Exception e) {
	        component = null;
	      }
	     } else {
	      // Get the current document
	      XDesktop desktop = (XDesktop) UnoRuntime.queryInterface(XDesktop.class,desktopFrame);
	      component = desktop.getCurrentComponent();
	     }

	     // Get the Writer document of the component
	     XModel model = (XModel)UnoRuntime.queryInterface(XModel.class, component);
	     if (model != null) {
	      System.out.println("File URL: "+model.getURL());
	     }
	   
	     if(model == null) {
	      // Either the document couldn't be loaded or there wasn't a current
	      // document. Whatever the reason has been, we create a new document.
	      component = componentloader.loadComponentFromURL("private:factory/swriter", "_blank", 0, new PropertyValue[0]);
	      model = (XModel) UnoRuntime.queryInterface(XModel.class, component);
	     }

	     showMessageBoxExamples(model);
	   } catch (BootstrapException e) {
	     e.printStackTrace();
	   } catch (Exception e) {
	     e.printStackTrace();
	   } finally {
	     System.exit(0);
	   }
	  }
	}
