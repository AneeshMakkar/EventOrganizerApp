package comp3350.plarty.presentation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Handles dialogs that give the user feedback about their actions, including
 * Success messages, Error messages, and Confirm messages.
 */
public class FeedbackDialog {

	/**
	 * Creates a simple Dialog that can be dismissed.
	 */
	public static void create(Context context, String title, String message) {
		AlertDialog.Builder dialogBuilder = getBuilder(context, title, message);
		dialogBuilder.setNegativeButton("Close", null);
		dialogBuilder.create().show();
	}

	/**
	 * Creates a Dialog that, when dismissed, exits its calling Activity.
	 */
	public static void createAndFinish(Context context, String title, String message) {
		AlertDialog.Builder dialogBuilder = getBuilder(context, title, message);
		dialogBuilder.setNegativeButton("Close", null);
		AlertDialog dialog = dialogBuilder.create();
		dialog.setOnDismissListener(dialogInterface -> ((Activity)context).finish());
		dialog.show();
	}

	/**
	 * Creates a Dialog with a yes/no choice, requiring the caller to specify
	 * behaviour.
	 */
	public static void createChoice(Context context, String title, String message, DialogInterface.OnClickListener onYes, DialogInterface.OnClickListener onNo) {
		AlertDialog.Builder dialogBuilder = getBuilder(context, title, message);
		dialogBuilder.setPositiveButton("Yes", onYes);
		dialogBuilder.setNegativeButton("No", onNo);
		dialogBuilder.create().show();
	}

	/**
	 * Initializes a Dialog builder with base information each Dialog has in common.
	 */
	private static AlertDialog.Builder getBuilder(Context context, String title, String message) {
		AlertDialog.Builder msgBuilder = new AlertDialog.Builder(context);
		msgBuilder.setTitle(title);
		msgBuilder.setMessage(message);
		return msgBuilder;
	}
}
