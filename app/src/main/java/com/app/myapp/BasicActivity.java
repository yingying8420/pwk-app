package com.app.myapp;


import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Window;

import org.king.activity.BaseActivity;

/**
 * 全局工具设置类(测试活动展现区发布)
 *
 * @author 张明杨
 *
 */
public class BasicActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/* 不允许横竖屏切换 */
//		setRequestedOrientation(1);
		/* 去除标题栏 */
		requestWindowFeature(Window.FEATURE_NO_TITLE);

	}

	// ----------------------------------------------------各种消息弹窗--------------------------------------------------------------------------//
	/**
	 * 带点击事件的单按钮AlertDialog
	 *
	 * @param title
	 *            弹框标题
	 * @param message
	 *            弹框消息内容
	 * @param positiveButton
	 *            弹框单按钮文字
	 * @param dialogClickListener
	 *            弹框按钮响应事件
	 */
	public void showAlertDialog(String title, String message,
								String positiveButton,
								DialogInterface.OnClickListener dialogClickListener) {
		new AlertDialog.Builder(context).setCancelable(false).setTitle(title)
				.setMessage(message)
				.setPositiveButton(positiveButton, dialogClickListener).show();
	}

	/**
	 * 带点击事件的双按钮AlertDialog
	 *
	 * @param title
	 *            弹框标题
	 * @param message
	 *            弹框消息内容
	 * @param positiveButton
	 *            弹框第一个按钮的文字
	 * @param negativeButton
	 *            弹框第二个按钮的文字
	 * @param positiveClickListener
	 *            弹框第一个按钮的单击事件
	 * @param negativeClickListener
	 *            弹框第二个按钮的单击事件
	 */
	public void showAlertDialog(String title, String message,
								String positiveButton, String negativeButton,
								DialogInterface.OnClickListener positiveClickListener,
								DialogInterface.OnClickListener negativeClickListener) {
		new AlertDialog.Builder(context).setCancelable(false).setTitle(title)
				.setMessage(message)
				.setPositiveButton(positiveButton, positiveClickListener)
				.setNegativeButton(negativeButton, negativeClickListener)
				.show();

	}

	/**
	 * 不带点击事件的消息弹出框
	 *
	 * @param title
	 *            弹框标题
	 * @param message
	 *            弹框消息内容
	 * @param positiveButton
	 *            弹框按钮文字
	 */
	public void showAlertDialog(String title, String message,
								String positiveButton) {
		new AlertDialog.Builder(context)
				// 设置按系统返回键的时候按钮弹窗不取消
				.setCancelable(false)
				.setTitle(title)
				.setMessage(message)
				.setPositiveButton(positiveButton,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
												int which) {
								dialog.dismiss();
							}
						}).show();
	}

	/**
	 * 单选列表类型的弹出框
	 *
	 * @param cancelable
	 *            设置是否能让用户主动取消弹窗
	 *
	 * @param title
	 *            弹窗标题
	 * @param items
	 *            弹窗的列表数据源
	 * @param selectListener
	 *            弹窗列表选择事件
	 */
	public void showAlertDialog(boolean cancelable, String title,
								String items[], DialogInterface.OnClickListener selectListener) {
		new AlertDialog.Builder(context)
				// 设置按系统返回键的时候按钮弹窗不取消
				.setCancelable(cancelable).setTitle(title)
				.setItems(items, selectListener).show();
	}

	// ———————————————————————————————————————————————————————————获取当前系统时间———————————————————————————————————————————————————————————————————————————————————————————————————//
	/**
	 * 获取当前系统时间
	 *
	 * @return 当前系统时间
	 */
	public String getTime() {
		/* 获取当前系统时间 */
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String time = sdf.format(curDate);
		return time;
	}
}