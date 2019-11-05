package jp.sourceforge.andjong;

import jp.sourceforge.andjong.mahjong.Mahjong;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class Game extends Activity {
	private static final String TAG = "Andjong";

	private AndjongView mAndjongView;
	private LinearLayout root;
	
	private Mahjong mMahjong;
	
	private Thread mMahjongThread;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");

		// タイトルを表示しないようにする。
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// フルスクリーンにする。
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		root = new LinearLayout(this);
		root.setBackgroundColor(Color.BLACK);
		// Viewを作成する。
		mAndjongView = new AndjongView(this);
		root.addView(mAndjongView);
		setContentView(root);
		mAndjongView.requestFocus();
		// ゲームを開始する。
		mMahjong = new Mahjong(mAndjongView);
		mMahjongThread = new Thread(mMahjong);
		mMahjongThread.start();
	}
	
	private boolean first = true;
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus && first){
			first = false;
			LayoutParams lp = mAndjongView.getLayoutParams();
			int rootW = root.getWidth();
			int rootH = root.getHeight();
			
			if(rootW / 3 * 2 > rootH){
				//画面过宽的场合
				lp.height = lp.MATCH_PARENT;
				lp.width = rootH/2*3;
			}
			else{
				//画面过窄的场合
				lp.width = lp.MATCH_PARENT;
				lp.height = rootW/3*2;
			}
			
			root.setGravity(Gravity.CENTER);
			
		}
	}
	@Override
	protected void onDestroy() {
		mMahjongThread.interrupt();
		super.onDestroy();
	}
}
