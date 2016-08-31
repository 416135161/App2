package com.sjning.app2;

import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.sjning.app2.tools.NormalUtil;
import com.sjning.app2.tools.UserSession;

public class MainHelper {
	private Dialog dialog;

	public boolean showSendReportPhoneDlg(Activity activity) {
		if (!TextUtils.isEmpty(UserSession.getPhoneNo(activity
				.getApplicationContext()))) {
			if (isExpird(activity)) {
				showDialogExpird(activity);
				return true;
			}
		} else {
			showDialog(activity);
			return true;
		}
		return false;
	}

	private void showDialog(final Activity activity) {
		if (dialog == null) {
			View view = activity.getLayoutInflater().inflate(
					R.layout.send_report_phone, null);
			final EditText text1 = (EditText) view.findViewById(R.id.text1);
			final EditText text2 = (EditText) view.findViewById(R.id.text2);
			final Button button = (Button) view.findViewById(R.id.button);
			final Button btnCancle = (Button) view
					.findViewById(R.id.button_cancle);

			btnCancle.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					text1.setText("");
					text2.setText("");
				}
			});
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String phoneNo1 = text1.getEditableText().toString();
					String phoneNo2 = text2.getEditableText().toString();
					if (TextUtils.isEmpty(phoneNo1) || phoneNo1.length() != 11) {
						NormalUtil.displayMessage(activity, "输入的非手机号码！");
						// dialog.show();
						return;
					}

					if (TextUtils.isEmpty(phoneNo2) || phoneNo2.length() != 11) {
						NormalUtil.displayMessage(activity, "重新输入的非手机号码！");
						dialog.show();
						return;
					}

					if (!TextUtils.equals(phoneNo1, phoneNo2)) {
						NormalUtil.displayMessage(activity, "请两次输入相同秘钥！");
						dialog.show();
						return;
					}

					UserSession.setPhoneNo(activity.getApplicationContext(),
							phoneNo1);
					setDate(activity);
					((MainActivity) activity).initListView();
					dialog.dismiss();

				}
			});
			text1.setText(UserSession.getPhoneNo(activity
					.getApplicationContext()));
			dialog = new AlertDialog.Builder(activity).setView(view).create();
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
		} else
			dialog.show();
	}

	private void showDialogExpird(final Activity activity) {
		if (dialog == null) {
			View view = activity.getLayoutInflater().inflate(
					R.layout.send_report_phone, null);
			final EditText text1 = (EditText) view.findViewById(R.id.text1);
			text1.setHint("请输入旧秘钥");
			final EditText text2 = (EditText) view.findViewById(R.id.text2);
			text2.setHint("请输入新秘钥");
			final Button button = (Button) view.findViewById(R.id.button);
			final Button btnCancle = (Button) view
					.findViewById(R.id.button_cancle);

			btnCancle.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					text1.setText("");
					text2.setText("");
				}
			});
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String phoneNo1 = text1.getEditableText().toString();
					String phoneNo2 = text2.getEditableText().toString();
					
					if (TextUtils.isEmpty(phoneNo1) || phoneNo1.length() != 11) {
						NormalUtil.displayMessage(activity, "输入的非手机号码！");
						// dialog.show();
						return;
					}

					if (TextUtils.isEmpty(phoneNo2) || phoneNo2.length() != 11) {
						NormalUtil.displayMessage(activity, "重新输入的非手机号码！");
						dialog.show();
						return;
					}

					String phoneOld = UserSession.getPhoneNo(activity
							.getApplicationContext());

					if (TextUtils.equals(phoneOld, phoneNo1)) {
						UserSession.setPhoneNo(
								activity.getApplicationContext(), phoneNo2);
						setDate(activity);
						((MainActivity) activity).initListView();
						dialog.dismiss();
					} else {
						NormalUtil.displayMessage(activity, "请输入正确的旧秘钥！");
						dialog.show();
						return;
					}

				}
			});
			text1.setText(UserSession.getPhoneNo(activity
					.getApplicationContext()));
			dialog = new AlertDialog.Builder(activity).setView(view).create();
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
		} else
			dialog.show();
	}

	private boolean isExpird(Activity activity) {
		Calendar cal = Calendar.getInstance(Locale.getDefault());
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		String date = year + "" + month;
		if (month == Calendar.DECEMBER
				&& !TextUtils.equals(date, UserSession.getDate(activity))) {
			return true;
		}
		return false;
	}

	private void setDate(Activity activity) {
		Calendar cal = Calendar.getInstance(Locale.getDefault());
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		UserSession.setDate(activity, year + "" + month);
	}
}
