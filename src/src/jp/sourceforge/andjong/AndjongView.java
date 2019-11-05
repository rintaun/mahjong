package jp.sourceforge.andjong;

import jp.sourceforge.andjong.R;
import jp.sourceforge.andjong.DrawItem.PlayerInfo;
import static jp.sourceforge.andjong.DrawItem.*;
import jp.sourceforge.andjong.mahjong.EventIf;
import jp.sourceforge.andjong.mahjong.Fuuro;
import jp.sourceforge.andjong.mahjong.Hai;
import jp.sourceforge.andjong.mahjong.InfoUi;
import jp.sourceforge.andjong.mahjong.Hou;
import jp.sourceforge.andjong.mahjong.Mahjong;
import jp.sourceforge.andjong.mahjong.PlayerAction;
import jp.sourceforge.andjong.mahjong.SuteHai;
import jp.sourceforge.andjong.mahjong.Tehai;
import jp.sourceforge.andjong.mahjong.Yama;
import jp.sourceforge.andjong.mahjong.AgariScore.AgariInfo;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class AndjongView extends View implements EventIf {
	private static final String TAG = "AndjongView";

	private static final int VIRTUAL_DENSITY = Bitmap.DENSITY_NONE;

	/** アクティビティ */
	private Game m_game;

	/** 牌のイメージ */
	private Bitmap[] m_haiImage;
	/** 牌のイメージの幅 */
	private int m_haiImageWidth;
	/** 牌のイメージの高さ */
	private int m_haiImageHeight;

	/** 牌の幅 */
	private static final int HAI_WIDTH = 19;
	/** 牌の高さ */
	private static final int HAI_HEIGHT = 23;

	private static final int TSUMO_OFFSET = 2;
	// private static final int SELECT_OFFSET = 16;
	private static final int SELECT_OFFSET = 16 - 4;

	private static final int FUURO_LEFT = 320 - 2;

	/** 牌の裏のイメージ */
	private Bitmap mHaiUraImage;

	/** 横になった牌のイメージ */
	private Bitmap[] m_horizontalHaiImage;

	/** 隠している牌のイメージ */
	private Bitmap m_hideHaiImage;

	private Bitmap[] m_largeHaiImage;
	private int m_largeHaiImageWidth;
	// private int m_largeHaiImageHeight;

	/** 1000点棒のイメージ */
	private Bitmap mTenbou1000Image;
	/** 100点棒のイメージ */
	private Bitmap mTenbou100Image;

	/** 起家マークのイメージ */
	private Bitmap mChiichaImage;

	/** 背景のペイント */
	private Paint mBackgroundPaint;

	/*
	 * 赤ドラ
	 */

	private Bitmap[] m_haiRedImage;
	private Bitmap[] m_horizontalHaiRedImage;
	private Bitmap[] m_largeHaiRedImage;

	/*
	 * print
	 */

	private static final int ROUND = 5;
	private static final int PRINT_AREA_WIDTH = 150 - 10;
	private static final int PRINT_AREA_HEIGHT = 134 - 10;
	private static final int PRINT_AREA_LEFT = 90 + 150 + 5;
	private static final int PRINT_AREA_TOP = 90 + 5;
	private static final int PRINT_AREA_RIGHT = PRINT_AREA_LEFT + PRINT_AREA_WIDTH;
	private static final int PRINT_AREA_BOTTOM = PRINT_AREA_TOP + PRINT_AREA_HEIGHT;
	private static final int PRINT_TEXT_SIZE = 30;
	private Paint m_printPaint;
	private RectF m_printRect;

	/** メッセージエリアの幅 */
	private static final int MESSAGE_AREA_WIDTH = 88;
	// private static final int MESSAGE_AREA_WIDTH = 146;
	/** メッセージエリアの高さ */
	private static final int MESSAGE_AREA_HEIGHT = 40;
	// private static final int MESSAGE_AREA_HEIGHT = 143;

	/** メッセージエリアのLeft */
	private static final int MESSAGE_AREA_LEFT = 392;
	// private static final int MESSAGE_AREA_LEFT = 87;
	/** メッセージエリアのTop */
	private static final int MESSAGE_AREA_TOP = 280;
	// private static final int MESSAGE_AREA_MARGIN = 20;
	// private static final int MESSAGE_AREA_TOP = 176;
	/** メッセージエリアのRight */
	private static final int MESSAGE_AREA_RIGHT = MESSAGE_AREA_LEFT + MESSAGE_AREA_WIDTH;
	/** メッセージエリアのBottom */
	private static final int MESSAGE_AREA_BOTTOM = MESSAGE_AREA_TOP + MESSAGE_AREA_HEIGHT;

	/** メッセージのテキストサイズ */
	private static final int MESSAGE_TEXT_SIZE = 20;

	/** メッセージの枠の丸み */
	private static final int MESSAGE_ROUND = 10;

	/** メッセージのペイント */
	private Paint mMessagePaint;

	/** メッセージの枠 */
	private RectF mMessageRect;

	/*
	 * 情報エリア
	 */

	/** 情報エリアの幅 */
	private static final int INFO_AREA_WIDTH = 300;
	/** 情報エリアの高さ */
	private static final int INFO_AREA_HEIGHT = 134;

	/** 情報エリアのLeft */
	private static final int INFO_AREA_LEFT = 90;
	/** 情報エリアのTop */
	private static final int INFO_AREA_TOP = 90;
	/** 情報エリアのRight */
	private static final int INFO_AREA_RIGHT = INFO_AREA_LEFT + INFO_AREA_WIDTH;
	/** 情報エリアのBottom */
	private static final int INFO_AREA_BOTTOM = INFO_AREA_TOP + INFO_AREA_HEIGHT;

	/** 情報エリアの枠の丸み */
	private static final int INFO_ROUND = 5;

	/** 情報エリアのペイント */
	private Paint m_infoPaint;

	/** 情報エリアの枠 */
	private RectF m_infoRect;

	/*
	 * メニュー
	 */

	/** メニューの個数の最大値 */
	private static final int MENU_ITEM_MAX = 4;

	/** メニューの幅 */
	private static final int MENU_WIDTH = 94;
	// private static final int MENU_WIDTH = 78;
	/** メニューの高さ */
	private static final int MENU_HEIGHT = 66;
	// private static final int MENU_HEIGHT = 72;

	/** メニューエリアのtop */
	private static final int MENU_AREA_TOP = 250;
	// private static final int MENU_AREA_TOP = 406;
	/** メニューエリアのleft */
	private static final int MENU_AREA_LEFT = 0;
	/** メニューエリアのtopのマージン */
	private static final int MENU_AREA_TOP_MARGIN = 2;
	// private static final int MENU_AREA_TOP_MARGIN = 1;
	/** メニューエリアのleftのマージン */
	private static final int MENU_AREA_LEFT_MARGIN = 2;

	/** メニューの枠 */
	private RectF m_menuRect[];

	/** メニューのテキストサイズ */
	private static final int MENU_TEXT_SIZE = 24;

	/*
	 * 結果画面
	 */

	/** 結果画面のドラのtop */
	private static final int RESULT_DORAS_TOP = 2;
	/** 結果画面のドラのleft */
	private static final int RESULT_DORAS_LEFT = 2;

	/** 描画アイテム */
	private DrawItem m_drawItem = new DrawItem();

	/** InfoUI */
	private InfoUi m_infoUi;

	/** UIの名前 */
	private String mName;

	/** プレイヤーアクション */
	private PlayerAction m_playerAction;

	private static final int LEFT_OFFSET = 5;
	private static final int TOP_OFFSET = 26;

	/** 局のLeft */
	private static final int KYOKU_LEFT = 160 + LEFT_OFFSET;
	/** 局のTop */
	private static final int KYOKU_TOP = 85 + 11 + TOP_OFFSET;

	/** 局のテキストサイズ */
	private static final int KYOKU_TEXT_SIZE = 18;

	/** ドラのLeft */
	private static final int DORAS_LEFT = 112 + LEFT_OFFSET;
	/** ドラのTop */
	private static final int DORAS_TOP = 154 + TOP_OFFSET;

	/** リーチ棒のイメージのLeft */
	private static final int TENBOU_01000_MIN_IMAGE_LEFT = 100 + LEFT_OFFSET;
	/** リーチ棒のイメージのTop */
	private static final int TENBOU_01000_MIN_IMAGE_TOP = 141 + TOP_OFFSET;

	/** リーチ棒の数のLeft */
	private static final int REACHBOU_LEFT = TENBOU_01000_MIN_IMAGE_LEFT + 43;
	/** リーチ棒の数Top */
	private static final int REACHBOU_TOP = TENBOU_01000_MIN_IMAGE_TOP + 5;

	/** 小さいのテキストサイズ */
	private static final int MINI_TEXT_SIZE = 12;

	/** 点棒のLeft */
	private static final int[] TENBO_LEFT = { 160 + LEFT_OFFSET, 197 + LEFT_OFFSET, 160 + LEFT_OFFSET, 123 + LEFT_OFFSET };
	/** 点棒のTop */
	private static final int[] TENBO_TOP = { 135 + TOP_OFFSET, 123 + TOP_OFFSET, 111 + TOP_OFFSET, 123 + TOP_OFFSET };

	/** 本場のイメージのLeft */
	private static final int TENBOU_00100_MIN_IMAGE_LEFT = 170 + LEFT_OFFSET;
	/** 本場のイメージのTop */
	private static final int TENBOU_00100_MIN_IMAGE_TOP = TENBOU_01000_MIN_IMAGE_TOP;

	/** 本場の数のLeft */
	private static final int HONBA_LEFT = TENBOU_00100_MIN_IMAGE_LEFT + 43;
	/** 本場の数Top */
	private static final int HONBA_TOP = TENBOU_00100_MIN_IMAGE_TOP + 5;

	private String m_settingKyokusuu;

	public int getKyokusuu() {
		int kyokusuu = 3;
		if (m_settingKyokusuu.equals("hanchan")) {
			kyokusuu = 7;
		}
		Log.d(TAG, "kyokusuu = " + kyokusuu);
		return kyokusuu;
	}

	// 暫定。
	public boolean isKuitan() {
		return Settings.isKuitan(this.m_game);
	}

	// 暫定。
	public boolean isAkaDora() {
		return Settings.isAkaDora(this.m_game);
	}

	Paint debugPaint = new Paint();
	
	private Bitmap reprocessBitmap(Bitmap src){
		Bitmap bmp = Bitmap.createBitmap(src.getWidth(),src.getHeight(),Bitmap.Config.ARGB_4444);
		Log.e("SCALED?", src.getWidth()+","+src.getHeight());
		bmp.setDensity(VIRTUAL_DENSITY);
		Canvas cv = new Canvas(bmp);
		cv.setDensity(VIRTUAL_DENSITY);
		
		Rect rect = new Rect(0, 0, bmp.getWidth(), bmp.getHeight()); 
		
		cv.drawBitmap(src, rect, rect, null);
		
		//cv.drawRect(rect, debugPaint);
		
		Log.e("PROCESSED_DENSITY","="+bmp.getDensity());
		
		return bmp;
	}
	
	/**
	 * コンストラクタ
	 * 
	 * @param context
	 *            アクティビティ
	 */
	public AndjongView(Context context) {
		super(context);

		// アクティビティを保存する。
		this.m_game = (Game) context;

		// イメージを初期化する。
		initImage(getResources());

		// ペイントを初期化する。
		initPaint(getResources());

		// UIが初期化されるまで待つ。
		m_drawItem.setState(STATE_INIT_WAIT);

		// フォーカスを有効にする。
		setFocusable(true);
		setFocusableInTouchMode(true);

		m_settingKyokusuu = Settings.getKyokusuu(context);
		Log.d(TAG, "m_settingKyokusuu = " + m_settingKyokusuu);
	}

	private Bitmap m_playerImage;
	private Bitmap m_kamichaImage;
	private Bitmap m_toimenImage;
	private Bitmap m_shimochaImage;

	/**
	 * イメージを初期化する。
	 * 
	 * @param res
	 *            リソース
	 */
	private void initImage(Resources res) {
		m_playerImage =  reprocessBitmap( Bitmap.createBitmap(408, 94, Bitmap.Config.ARGB_4444));

		m_kamichaImage =reprocessBitmap( Bitmap.createBitmap(KAWA_TEHAI_AREA_HEIGHT, KAWA_TEHAI_AREA_WIDTH, Bitmap.Config.ARGB_4444));
		m_toimenImage =reprocessBitmap( Bitmap.createBitmap(KAWA_TEHAI_AREA_WIDTH, KAWA_TEHAI_AREA_HEIGHT, Bitmap.Config.ARGB_4444));
		m_shimochaImage = reprocessBitmap( Bitmap.createBitmap(KAWA_TEHAI_AREA_HEIGHT, KAWA_TEHAI_AREA_WIDTH, Bitmap.Config.ARGB_4444));

		m_haiImage = new Bitmap[Hai.ID_MAX + 1];

		m_haiImage[0] = BitmapFactory.decodeResource(res, R.drawable.hai_00_wan_1);
		m_haiImage[1] = BitmapFactory.decodeResource(res, R.drawable.hai_01_wan_2);
		m_haiImage[2] = BitmapFactory.decodeResource(res, R.drawable.hai_02_wan_3);
		m_haiImage[3] = BitmapFactory.decodeResource(res, R.drawable.hai_03_wan_4);
		m_haiImage[4] = BitmapFactory.decodeResource(res, R.drawable.hai_04_wan_5);
		m_haiImage[5] = BitmapFactory.decodeResource(res, R.drawable.hai_05_wan_6);
		m_haiImage[6] = BitmapFactory.decodeResource(res, R.drawable.hai_06_wan_7);
		m_haiImage[7] = BitmapFactory.decodeResource(res, R.drawable.hai_07_wan_8);
		m_haiImage[8] = BitmapFactory.decodeResource(res, R.drawable.hai_08_wan_9);

		m_haiImage[9] = BitmapFactory.decodeResource(res, R.drawable.hai_09_pin_1);
		m_haiImage[10] = BitmapFactory.decodeResource(res, R.drawable.hai_10_pin_2);
		m_haiImage[11] = BitmapFactory.decodeResource(res, R.drawable.hai_11_pin_3);
		m_haiImage[12] = BitmapFactory.decodeResource(res, R.drawable.hai_12_pin_4);
		m_haiImage[13] = BitmapFactory.decodeResource(res, R.drawable.hai_13_pin_5);
		m_haiImage[14] = BitmapFactory.decodeResource(res, R.drawable.hai_14_pin_6);
		m_haiImage[15] = BitmapFactory.decodeResource(res, R.drawable.hai_15_pin_7);
		m_haiImage[16] = BitmapFactory.decodeResource(res, R.drawable.hai_16_pin_8);
		m_haiImage[17] = BitmapFactory.decodeResource(res, R.drawable.hai_17_pin_9);

		m_haiImage[18] = BitmapFactory.decodeResource(res, R.drawable.hai_18_sou_1);
		m_haiImage[19] = BitmapFactory.decodeResource(res, R.drawable.hai_19_sou_2);
		m_haiImage[20] = BitmapFactory.decodeResource(res, R.drawable.hai_20_sou_3);
		m_haiImage[21] = BitmapFactory.decodeResource(res, R.drawable.hai_21_sou_4);
		m_haiImage[22] = BitmapFactory.decodeResource(res, R.drawable.hai_22_sou_5);
		m_haiImage[23] = BitmapFactory.decodeResource(res, R.drawable.hai_23_sou_6);
		m_haiImage[24] = BitmapFactory.decodeResource(res, R.drawable.hai_24_sou_7);
		m_haiImage[25] = BitmapFactory.decodeResource(res, R.drawable.hai_25_sou_8);
		m_haiImage[26] = BitmapFactory.decodeResource(res, R.drawable.hai_26_sou_9);

		m_haiImage[27] = BitmapFactory.decodeResource(res, R.drawable.hai_27_ton);
		m_haiImage[28] = BitmapFactory.decodeResource(res, R.drawable.hai_28_nan);
		m_haiImage[29] = BitmapFactory.decodeResource(res, R.drawable.hai_29_sha);
		m_haiImage[30] = BitmapFactory.decodeResource(res, R.drawable.hai_30_pei);

		m_haiImage[31] = BitmapFactory.decodeResource(res, R.drawable.hai_31_haku);
		m_haiImage[32] = BitmapFactory.decodeResource(res, R.drawable.hai_32_hatsu);
		m_haiImage[33] = BitmapFactory.decodeResource(res, R.drawable.hai_33_chun);

		for (int i = 0; i < m_haiImage.length; i++) {
			if (null != m_haiImage[i]) {
				m_haiImage[i] = reprocessBitmap( m_haiImage[i]);
			}
		}
		m_haiImageWidth = m_haiImage[0].getWidth();
		m_haiImageHeight = m_haiImage[0].getHeight();

		mHaiUraImage =reprocessBitmap( BitmapFactory.decodeResource(res, R.drawable.hai_ura));
		m_horizontalHaiImage = new Bitmap[Hai.ID_MAX + 1];

		for (int i = 0; i < m_horizontalHaiImage.length; i++) {
			m_horizontalHaiImage[i] =reprocessBitmap( createHorizontalBitmap(m_haiImage[i]));
		}

		m_hideHaiImage =reprocessBitmap(BitmapFactory.decodeResource(res, R.drawable.hai_hide));

		mTenbou1000Image =reprocessBitmap( BitmapFactory.decodeResource(res, R.drawable.tenbou_1000));

		mTenbou100Image =reprocessBitmap( BitmapFactory.decodeResource(res, R.drawable.tenbou_100));
		mChiichaImage = reprocessBitmap(BitmapFactory.decodeResource(res, R.drawable.chiicha));
		/*
		 * 大きい牌のイメージ
		 */

		m_largeHaiImage = new Bitmap[Hai.ID_MAX + 1];

		m_largeHaiImage[0] = BitmapFactory.decodeResource(res, R.drawable.hai_00_wan_1_l);
		m_largeHaiImage[1] = BitmapFactory.decodeResource(res, R.drawable.hai_01_wan_2_l);
		m_largeHaiImage[2] = BitmapFactory.decodeResource(res, R.drawable.hai_02_wan_3_l);
		m_largeHaiImage[3] = BitmapFactory.decodeResource(res, R.drawable.hai_03_wan_4_l);
		m_largeHaiImage[4] = BitmapFactory.decodeResource(res, R.drawable.hai_04_wan_5_l);
		m_largeHaiImage[5] = BitmapFactory.decodeResource(res, R.drawable.hai_05_wan_6_l);
		m_largeHaiImage[6] = BitmapFactory.decodeResource(res, R.drawable.hai_06_wan_7_l);
		m_largeHaiImage[7] = BitmapFactory.decodeResource(res, R.drawable.hai_07_wan_8_l);
		m_largeHaiImage[8] = BitmapFactory.decodeResource(res, R.drawable.hai_08_wan_9_l);

		m_largeHaiImage[9] = BitmapFactory.decodeResource(res, R.drawable.hai_09_pin_1_l);
		m_largeHaiImage[10] = BitmapFactory.decodeResource(res, R.drawable.hai_10_pin_2_l);
		m_largeHaiImage[11] = BitmapFactory.decodeResource(res, R.drawable.hai_11_pin_3_l);
		m_largeHaiImage[12] = BitmapFactory.decodeResource(res, R.drawable.hai_12_pin_4_l);
		m_largeHaiImage[13] = BitmapFactory.decodeResource(res, R.drawable.hai_13_pin_5_l);
		m_largeHaiImage[14] = BitmapFactory.decodeResource(res, R.drawable.hai_14_pin_6_l);
		m_largeHaiImage[15] = BitmapFactory.decodeResource(res, R.drawable.hai_15_pin_7_l);
		m_largeHaiImage[16] = BitmapFactory.decodeResource(res, R.drawable.hai_16_pin_8_l);
		m_largeHaiImage[17] = BitmapFactory.decodeResource(res, R.drawable.hai_17_pin_9_l);

		m_largeHaiImage[18] = BitmapFactory.decodeResource(res, R.drawable.hai_18_sou_1_l);
		m_largeHaiImage[19] = BitmapFactory.decodeResource(res, R.drawable.hai_19_sou_2_l);
		m_largeHaiImage[20] = BitmapFactory.decodeResource(res, R.drawable.hai_20_sou_3_l);
		m_largeHaiImage[21] = BitmapFactory.decodeResource(res, R.drawable.hai_21_sou_4_l);
		m_largeHaiImage[22] = BitmapFactory.decodeResource(res, R.drawable.hai_22_sou_5_l);
		m_largeHaiImage[23] = BitmapFactory.decodeResource(res, R.drawable.hai_23_sou_6_l);
		m_largeHaiImage[24] = BitmapFactory.decodeResource(res, R.drawable.hai_24_sou_7_l);
		m_largeHaiImage[25] = BitmapFactory.decodeResource(res, R.drawable.hai_25_sou_8_l);
		m_largeHaiImage[26] = BitmapFactory.decodeResource(res, R.drawable.hai_26_sou_9_l);

		m_largeHaiImage[27] = BitmapFactory.decodeResource(res, R.drawable.hai_27_ton_l);
		m_largeHaiImage[28] = BitmapFactory.decodeResource(res, R.drawable.hai_28_nan_l);
		m_largeHaiImage[29] = BitmapFactory.decodeResource(res, R.drawable.hai_29_sha_l);
		m_largeHaiImage[30] = BitmapFactory.decodeResource(res, R.drawable.hai_30_pei_l);

		m_largeHaiImage[31] = BitmapFactory.decodeResource(res, R.drawable.hai_31_haku_l);
		m_largeHaiImage[32] = BitmapFactory.decodeResource(res, R.drawable.hai_32_hatsu_l);
		m_largeHaiImage[33] = BitmapFactory.decodeResource(res, R.drawable.hai_33_chun_l);

		m_largeHaiImageWidth = m_largeHaiImage[0].getWidth();
		// m_largeHaiImageHeight = m_largeHaiImage[0].getHeight();

		/*
		 * 赤ドラのイメージ
		 */
		for (int i = 0; i < m_largeHaiImage.length; i++) {
			if (null != m_largeHaiImage[i]) {
				m_largeHaiImage[i] = reprocessBitmap(m_largeHaiImage[i]);
			}
		}
		m_haiRedImage = new Bitmap[Hai.ID_MAX + 1];
		m_haiRedImage[4] = BitmapFactory.decodeResource(res, R.drawable.hai_04_wan_5_red);
		m_haiRedImage[13] = BitmapFactory.decodeResource(res, R.drawable.hai_13_pin_5_red);
		m_haiRedImage[22] = BitmapFactory.decodeResource(res, R.drawable.hai_22_sou_5_red);

		m_horizontalHaiRedImage = new Bitmap[Hai.ID_MAX + 1];
		m_horizontalHaiRedImage[4] = createHorizontalBitmap(m_haiRedImage[4]);
		m_horizontalHaiRedImage[13] = createHorizontalBitmap(m_haiRedImage[13]);
		m_horizontalHaiRedImage[22] = createHorizontalBitmap(m_haiRedImage[22]);

		m_largeHaiRedImage = new Bitmap[Hai.ID_MAX + 1];
		m_largeHaiRedImage[4] = BitmapFactory.decodeResource(res, R.drawable.hai_04_wan_5_l_red);
		m_largeHaiRedImage[13] = BitmapFactory.decodeResource(res, R.drawable.hai_13_pin_5_l_red);
		m_largeHaiRedImage[22] = BitmapFactory.decodeResource(res, R.drawable.hai_22_sou_5_l_red);

		for (int i = 0; i < m_haiRedImage.length; i++) {
			if (null != m_haiRedImage[i]) {
				m_haiRedImage[i]=reprocessBitmap(m_haiRedImage[i]);
			}
		}

		for (int i = 0; i < m_horizontalHaiRedImage.length; i++) {
			if (null != m_horizontalHaiRedImage[i]) {
				m_horizontalHaiRedImage[i]=reprocessBitmap(m_horizontalHaiRedImage[i]);
			}
		}

		for (int i = 0; i < m_largeHaiRedImage.length; i++) {
			if (null != m_largeHaiRedImage[i]) {
				m_largeHaiRedImage[i]=reprocessBitmap(m_largeHaiRedImage[i]);
			}
		}
	}

	private Bitmap getHaiImage(Hai a_hai) {
		if (a_hai.isRed()) {
			return m_haiRedImage[a_hai.getId()];
		} else {
			return m_haiImage[a_hai.getId()];
		}
	}

	private Bitmap getHorizontalHaiImage(Hai a_hai) {
		if (a_hai.isRed()) {
			return m_horizontalHaiRedImage[a_hai.getId()];
		} else {
			return m_horizontalHaiImage[a_hai.getId()];
		}
	}

	private Bitmap getLargeHaiImage(Hai a_hai) {
		if (a_hai.isRed()) {
			return m_largeHaiRedImage[a_hai.getId()];
		} else {
			return m_largeHaiImage[a_hai.getId()];
		}
	}

	/**
	 * ペイントを初期化する。
	 * 
	 * @param res
	 *            リソース
	 */
	private void initPaint(Resources res) {
		mBackgroundPaint = new Paint();
		mBackgroundPaint.setColor(res.getColor(R.color.andjong_background));

		mMessagePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		// mMessagePaint.setColor(R.color.menu);
		mMessagePaint.setColor(res.getColor(R.color.menu));
		mMessageRect = new RectF(MESSAGE_AREA_LEFT, MESSAGE_AREA_TOP, MESSAGE_AREA_RIGHT + MESSAGE_ROUND, MESSAGE_AREA_BOTTOM + MESSAGE_ROUND);

		// メニューを初期化する。
		m_menuRect = new RectF[MENU_ITEM_MAX];
		int left;
		int top = MENU_AREA_TOP + MENU_AREA_TOP_MARGIN;
		int right;
		int bottom = top + MENU_HEIGHT;
		for (int i = 0; i < m_menuRect.length; i++) {
			left = (MENU_AREA_LEFT + MENU_AREA_LEFT_MARGIN) + ((MENU_WIDTH + (MENU_AREA_LEFT_MARGIN * 2)) * i);
			right = left + MENU_WIDTH;
			m_menuRect[m_menuRect.length - 1 - i] = new RectF(left, top, right, bottom);
			// m_menuRect[i] = new RectF(left, top, right, bottom);
		}

		m_infoPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		m_infoPaint.setColor(res.getColor(R.color.info));
		m_infoRect = new RectF(INFO_AREA_LEFT, INFO_AREA_TOP, INFO_AREA_RIGHT, INFO_AREA_BOTTOM);

		m_printPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		m_printPaint.setColor(Color.WHITE);
		m_printPaint.setAlpha(192);
		m_printRect = new RectF(PRINT_AREA_LEFT, PRINT_AREA_TOP, PRINT_AREA_RIGHT, PRINT_AREA_BOTTOM);
	}

	/**
	 * 横になったイメージを作成する。
	 * 
	 * @param verticalImage
	 *            縦のイメージ
	 * @return 横になったイメージ
	 */
	private Bitmap createHorizontalBitmap(Bitmap verticalImage) {
		int height = verticalImage.getWidth();
		int width = verticalImage.getHeight();
		Bitmap horizontalImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(horizontalImage);
		canvas.rotate(270, 0, 0);
		canvas.drawBitmap(verticalImage, -height, 0, null);
		return horizontalImage;
	}

	/**
	 * UIを初期化する。
	 * 
	 * @param infoUi
	 *            InfoUi
	 * @param name
	 *            UIの名前
	 */
	public void initUi(InfoUi infoUi, String name) {
		this.m_infoUi = infoUi;
		this.mName = name;

		// プレイヤーアクションを取得する。
		m_playerAction = m_infoUi.getPlayerAction();

		// 状態なしにしておく。
		m_drawItem.setState(STATE_NONE);
	}

	boolean m_isValidChiiLeft;
	boolean m_isValidChiiCenter;
	boolean m_isValidChiiRight;

	private Bitmap bufferBitmap;
	private Canvas a_canvas;

	@Override
	protected void onDraw(Canvas cccc) {
		if (null == bufferBitmap) {
			bufferBitmap = Bitmap.createBitmap(480, 320, Bitmap.Config.ARGB_4444);
			bufferBitmap.setDensity(VIRTUAL_DENSITY);
			a_canvas = new Canvas(bufferBitmap);
		}

		try {
			Resources res = getResources();
			// 背景を描画する。
			a_canvas.drawRect(0, 0, 480, 320, mBackgroundPaint);

			Log.e("BITMAPDEBUG", "ORIGNAL_WIDTH=" + m_playerImage.getWidth());
			Log.e("BITMAPDEBUG", "DENSITY_WIDTH=" + m_playerImage.getScaledWidth(VIRTUAL_DENSITY));
			Log.e("BITMAPDEBUG", "CANVAS_WIDTH=" + m_playerImage.getScaledWidth(a_canvas));
			Log.e("BITMAPDEBUG", "BITMAP_DENSITY=" + m_playerImage.getDensity());
			Log.e("BITMAPDEBUG", "CANVAS_DENSITY=" + (a_canvas).getDensity());

			synchronized (m_drawItem) {
				if (m_drawItem.m_state == STATE_KYOKU_START) {
					// 局を表示する。
					drawString(240, 160, a_canvas, 30, Color.WHITE, m_drawItem.getKyokuString(), Align.CENTER);
					return;
				} else if (m_drawItem.m_state == STATE_RESULT) {
					// ドラを表示する。
					drawDoraHais(RESULT_DORAS_LEFT, RESULT_DORAS_TOP, a_canvas, m_infoUi.getDoraHais());

					SuteHai[] suteHais = m_drawItem.m_playerInfos[m_drawItem.m_kazeFrom].m_kawa.getSuteHais();
					int kawaLength = m_drawItem.m_playerInfos[m_drawItem.m_kazeFrom].m_kawa.getSuteHaisLength();
					for (int i = 0; i < kawaLength; i++) {
						if (suteHais[i].isReach()) {
							// 裏ドラを表示する。
							drawDoraHais(RESULT_DORAS_LEFT, RESULT_DORAS_TOP + HAI_HEIGHT, a_canvas, m_infoUi.getUraDoraHais());
							break;
						}
					}

					if (m_drawItem.m_eventId == EventId.TSUMO_AGARI) {
						drawPlayerTehai(a_canvas, m_drawItem.m_playerInfos[m_drawItem.m_kazeFrom].m_tehai, m_drawItem.m_playerInfos[m_drawItem.m_kazeFrom].m_tsumoHai, 15, 2, 50);
					} else {
						drawPlayerTehai(a_canvas, m_drawItem.m_playerInfos[m_drawItem.m_kazeFrom].m_tehai, m_drawItem.m_suteHai, 15, 2, 50);
					}

					AgariInfo agariInfo = m_infoUi.getAgariInfo();
					int left = 2;
					int top = 50 + 44 + 2 + 10;
					for (int i = 0; i < agariInfo.m_yakuNames.length; i++) {
						drawString(left, top, a_canvas, 18, Color.WHITE, agariInfo.m_yakuNames[i], Align.LEFT);
						top += 18;
					}
					String string = new String();
					String han = res.getString(R.string.han);
					String ten = res.getString(R.string.ten);
					String fu = res.getString(R.string.fu);
					String sp = res.getString(R.string.space);
					if (agariInfo.m_score.m_oyaRon >= 48000) {
						string += res.getString(R.string.yakuman) + sp + agariInfo.m_agariScore + ten;
					} else if (agariInfo.m_score.m_oyaRon >= 36000) {
						string += agariInfo.m_han + han + sp + res.getString(R.string.sanbaiman) + sp + agariInfo.m_agariScore + ten;
					} else if (agariInfo.m_score.m_oyaRon >= 24000) {
						string += agariInfo.m_han + han + sp + res.getString(R.string.baiman) + sp + agariInfo.m_agariScore + ten;
					} else if (agariInfo.m_score.m_oyaRon >= 18000) {
						string += agariInfo.m_han + han + sp + res.getString(R.string.haneman) + sp + agariInfo.m_agariScore + ten;
					} else if (agariInfo.m_score.m_oyaRon >= 12000) {
						string += agariInfo.m_han + han + sp + agariInfo.m_fu + fu + sp + res.getString(R.string.mangan) + sp + agariInfo.m_agariScore + ten;
					} else {
						string += agariInfo.m_han + han + sp + agariInfo.m_fu + fu + sp + agariInfo.m_agariScore + ten;
					}
					drawString(left, top + 20, a_canvas, 20, Color.WHITE, string, Align.LEFT);
					return;
				}

				a_canvas.drawRoundRect(m_infoRect, INFO_ROUND, INFO_ROUND, m_infoPaint);

				switch (m_drawItem.m_state) {
				case STATE_INIT_WAIT:
				case STATE_NONE:
					// 何も描画しない。
					return;
				case STATE_REACH:
					// リーチのメッセージを表示する。
					drawPrint(a_canvas, res.getString(R.string.info_reach));
					break;
				case STATE_RON:
					// ロンのメッセージを表示する。
					drawPrint(a_canvas, res.getString(R.string.info_ron));
					break;
				case STATE_TSUMO:
					// ツモのメッセージを表示する。
					drawPrint(a_canvas, res.getString(R.string.info_tsumo));
					break;
				case STATE_RYUUKYOKU:
					// 流局のメッセージを表示する。
					drawPrint(a_canvas, res.getString(R.string.info_ryuukyoku));
					break;
				case STATE_END:
					drawPrint(a_canvas, res.getString(R.string.info_end));
					break;
				}

				// 局を表示する。
				drawString(KYOKU_LEFT - 30, KYOKU_TOP, a_canvas, KYOKU_TEXT_SIZE, Color.WHITE, m_drawItem.getKyokuString(), Align.CENTER);

				// 残り牌を表示する。
				drawString(KYOKU_LEFT + 40, KYOKU_TOP, a_canvas, KYOKU_TEXT_SIZE, Color.WHITE, new Integer(m_drawItem.m_tsumoRemain).toString(), Align.CENTER);

				// リーチ棒の数を表示する。
				drawReachbou(a_canvas, m_drawItem.getReachbou());

				// 本場を表示する。
				drawHonba(a_canvas, m_drawItem.getHonba());

				// ドラを表示する。
				drawDoraHais(DORAS_LEFT, DORAS_TOP, a_canvas, m_infoUi.getDoraHais());

				int manKaze = m_infoUi.getManKaze();
				int dispKaze[] = { 0, 1, 2, 3 };
				for (int i = 0; i < 4; i++) {
					dispKaze[i] = manKaze;
					manKaze++;
					manKaze %= 4;
				}

				String[] kazeStrings = res.getStringArray(R.array.kaze);
				String sp = res.getString(R.string.space);

				// 点棒を表示する。
				for (int i = 0; i < EventIf.KAZE_KIND_NUM; i++) {
					drawString(TENBO_LEFT[i], TENBO_TOP[i], a_canvas, MINI_TEXT_SIZE, Color.WHITE, kazeStrings[dispKaze[i]] + sp + new Integer(m_drawItem.m_playerInfos[dispKaze[i]].m_tenbo).toString(), Align.CENTER);
				}

				// 起家マークを表示する。
				drawChiicha(a_canvas, m_drawItem.getChiicha());

				Bitmap test3 = getKawaTehaiAreaImage(m_drawItem.m_playerInfos[dispKaze[1]].m_tehai, m_drawItem.m_playerInfos[dispKaze[1]].m_kawa, PLACE_KAMICHA, dispKaze[1], m_drawItem.m_playerInfos[dispKaze[1]].m_tenpai, m_drawItem.m_playerInfos[dispKaze[1]].m_tsumoHai);
				a_canvas.drawBitmap(test3, KAWA_TEHAI_AREA_KAMICHA_LEFT, KAWA_TEHAI_AREA_KAMICHA_TOP, null);

				Bitmap test = getKawaTehaiAreaImage(m_drawItem.m_playerInfos[dispKaze[2]].m_tehai, m_drawItem.m_playerInfos[dispKaze[2]].m_kawa, PLACE_TOIMEN, dispKaze[2], m_drawItem.m_playerInfos[dispKaze[2]].m_tenpai, m_drawItem.m_playerInfos[dispKaze[2]].m_tsumoHai);
				a_canvas.drawBitmap(test, KAWA_TEHAI_AREA_TOIMEN_LEFT, KAWA_TEHAI_AREA_TOIMEN_TOP, null);

				Bitmap test4 = getKawaTehaiAreaImage(m_drawItem.m_playerInfos[dispKaze[3]].m_tehai, m_drawItem.m_playerInfos[dispKaze[3]].m_kawa, PLACE_SHIMOCHA, dispKaze[3], m_drawItem.m_playerInfos[dispKaze[3]].m_tenpai, m_drawItem.m_playerInfos[dispKaze[3]].m_tsumoHai);
				a_canvas.drawBitmap(test4, KAWA_TEHAI_AREA_SHIMOCHA_LEFT, KAWA_TEHAI_AREA_SHIMOCHA_TOP, null);

				Bitmap test2 = getKawaTehaiAreaImage(m_drawItem.m_playerInfos[dispKaze[0]].m_tehai, m_drawItem.m_playerInfos[dispKaze[0]].m_kawa, PLACE_PLAYER, dispKaze[0], true, m_drawItem.m_playerInfos[dispKaze[0]].m_tsumoHai);
				a_canvas.drawBitmap(test2, KAWA_TEHAI_AREA_PLAYER_LEFT, KAWA_TEHAI_AREA_PLAYER_TOP, null);

				// アクションボタンを表示する。
				boolean actionRequest = m_playerAction.isActionRequest();
				if (actionRequest) {
					if (!m_playerAction.isDispMenu()) {
						drawMessage(a_canvas, res.getString(R.string.button_menu));
					} else {
						int iMenu = 0;

						if (m_playerAction.isValidReach()) {
							drawMenuMessage(a_canvas, res.getString(R.string.button_reach), iMenu);
							iMenu++;
						}

						if (m_playerAction.isValidRon()) {
							drawMenuMessage(a_canvas, res.getString(R.string.button_ron), iMenu);
							iMenu++;
						}

						if (m_playerAction.isValidTsumo()) {
							drawMenuMessage(a_canvas, res.getString(R.string.button_tsumo), iMenu);
							iMenu++;
						}

						if (m_playerAction.isValidPon()) {
							drawMenuMessage(a_canvas, res.getString(R.string.button_pon), iMenu);
							iMenu++;
						}

						m_isValidChiiLeft = m_playerAction.isValidChiiLeft();
						m_isValidChiiCenter = m_playerAction.isValidChiiCenter();
						m_isValidChiiRight = m_playerAction.isValidChiiRight();

						if (m_isValidChiiLeft || m_isValidChiiCenter || m_isValidChiiRight) {
							drawMenuMessage(a_canvas, res.getString(R.string.button_chii), iMenu);
							iMenu++;
						}

						if (m_playerAction.isValidKan() || m_playerAction.isValidDaiMinKan()) {
							drawMenuMessage(a_canvas, res.getString(R.string.button_kan), iMenu);
							iMenu++;
						}
					}
				}
			}
		} finally {
			// canvas.drawBitmap(bufferBitmap, 0, 0,getWidth(),getHeight(),
			// null);
			bufferDest.right = getWidth();
			bufferDest.bottom = getHeight();
			cccc.drawBitmap(bufferBitmap, bufferSrc, bufferDest, null);
		}
	}

	private Rect bufferSrc = new Rect(0, 0, 480, 320);
	private Rect bufferDest = new Rect(0, 0, 1, 1);

	/**
	 * メッセージを表示する。
	 * 
	 * @param canvas
	 *            キャンバス
	 * @param string
	 *            文字列
	 */
	private void drawMessage(Canvas canvas, String string) {
		canvas.drawRoundRect(mMessageRect, MESSAGE_ROUND, MESSAGE_ROUND, mMessagePaint);
		drawString((MESSAGE_AREA_LEFT + MESSAGE_AREA_RIGHT) / 2, (MESSAGE_AREA_TOP + MESSAGE_AREA_BOTTOM) / 2, canvas, MESSAGE_TEXT_SIZE, Color.WHITE, string, Align.CENTER);
	}

	private void drawMenuMessage(Canvas canvas, String string, int no) {
		canvas.drawRoundRect(m_menuRect[no], ROUND, ROUND, mMessagePaint);
		drawString((int) (m_menuRect[no].left + (MENU_WIDTH / 2)), (int) (m_menuRect[no].top + (MENU_HEIGHT / 4)), canvas, MENU_TEXT_SIZE, Color.WHITE, string, Align.CENTER);
		// drawString((int) (m_menuRect[no].left + (MENU_WIDTH / 2)), (int)
		// (m_menuRect[no].top + (MENU_HEIGHT / 2)), canvas, MENU_TEXT_SIZE,
		// Color.WHITE, string, Align.CENTER);
	}

	private void drawPrint(Canvas a_canvas, String a_string) {
		a_canvas.drawRoundRect(m_printRect, ROUND, ROUND, m_printPaint);
		drawString((PRINT_AREA_LEFT + PRINT_AREA_RIGHT) / 2, (PRINT_AREA_TOP + PRINT_AREA_BOTTOM) / 2, a_canvas, PRINT_TEXT_SIZE, Color.BLACK, a_string, Align.CENTER);
	}

	/**
	 * 文字列を表示する。
	 * 
	 * @param left
	 *            Left
	 * @param top
	 *            Top
	 * @param canvas
	 *            キャンバス
	 * @param textSize
	 *            テキストサイズ
	 * @param color
	 *            色
	 * @param string
	 *            文字列
	 */
	private void drawString(int left, int top, Canvas canvas, int textSize, int color, String string, Align a_align) {
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(color);
		paint.setTextSize(textSize);
		paint.setTextAlign(a_align);

		canvas.drawText(string, left, top - ((paint.ascent() + paint.descent()) / 2), paint);
	}

	/**
	 * ドラを表示する。
	 * 
	 * @param a_left
	 *            left
	 * @param a_top
	 *            top
	 * @param a_canvas
	 *            キャンバス
	 * @param a_hais
	 *            牌の配列
	 */
	private void drawDoraHais(int a_left, int a_top, Canvas a_canvas, Hai a_hais[]) {
		int i = 0;
		for (; i < a_hais.length; i++) {
			a_canvas.drawBitmap(getHaiImage(a_hais[i]), a_left + (i * HAI_WIDTH), a_top, null);
		}

		for (; i < Yama.DORA_HAIS_MAX; i++) {
			a_canvas.drawBitmap(mHaiUraImage, a_left + (i * HAI_WIDTH), a_top, null);
		}
	}

	/**
	 * リーチ棒の数を表示する。
	 * 
	 * @param canvas
	 *            キャンバス
	 * @param reachbou
	 *            リーチ棒の数
	 */
	private void drawReachbou(Canvas canvas, int reachbou) {
		canvas.drawBitmap(mTenbou1000Image, TENBOU_01000_MIN_IMAGE_LEFT, TENBOU_01000_MIN_IMAGE_TOP, null);

		Resources res = getResources();
		String string = res.getString(R.string.multi) + res.getString(R.string.space) + new Integer(reachbou).toString();
		drawString(REACHBOU_LEFT, REACHBOU_TOP, canvas, MINI_TEXT_SIZE, Color.WHITE, string, Align.CENTER);
	}

	/**
	 * 本場を表示する。
	 * 
	 * @param canvas
	 *            キャンバス
	 * @param honba
	 *            本場
	 */
	private void drawHonba(Canvas canvas, int honba) {
		canvas.drawBitmap(mTenbou100Image, TENBOU_00100_MIN_IMAGE_LEFT, TENBOU_00100_MIN_IMAGE_TOP, null);

		Resources res = getResources();
		String string = res.getString(R.string.multi) + res.getString(R.string.space) + new Integer(honba).toString();
		drawString(HONBA_LEFT, HONBA_TOP, canvas, MINI_TEXT_SIZE, Color.WHITE, string, Align.CENTER);
	}

	/**
	 * 起家マークを表示する。
	 * 
	 * @param canvas
	 *            キャンバス
	 * @param chiicha
	 *            起家
	 */
	private void drawChiicha(Canvas canvas, int chiicha) {
		canvas.drawBitmap(mChiichaImage, TENBO_LEFT[chiicha] + 29, TENBO_TOP[chiicha] - 5, null);
		// canvas.drawBitmap(mChiichaImage, TENBO_LEFT[chiicha] - 26,
		// TENBO_TOP[chiicha] - 5, null);
	}

	private static final int PLACE_PLAYER = 0;
	private static final int PLACE_KAMICHA = 1;
	private static final int PLACE_TOIMEN = 2;
	private static final int PLACE_SHIMOCHA = 3;

	private static final int KAWA_TEHAI_AREA_WIDTH = 320;
	private static final int KAWA_TEHAI_AREA_HEIGHT = 88;
	// private static final int KAWA_TEHAI_AREA_HEIGHT = 85;

	private static final int TEHAI_LEFT = 4;
	// private static final int TEHAI_LEFT = 2;
	// private static final int TEHAI_TOP = 47;
	private static final int TEHAI_TOP = 48;

	private static final int KAWA_LEFT = 42;
	// private static final int KAWA_LEFT = 49;
	private static final int KAWA_TOP = 0;

	private static final int KAWA_TEHAI_AREA_PLAYER_LEFT = 42;
	private static final int KAWA_TEHAI_AREA_PLAYER_TOP = 226;
	// private static final int KAWA_TEHAI_AREA_PLAYER_LEFT = 0;
	// private static final int KAWA_TEHAI_AREA_PLAYER_TOP = 321;

	private static final int KAWA_TEHAI_AREA_TOIMEN_LEFT = 72;
	// private static final int KAWA_TEHAI_AREA_TOIMEN_LEFT = 0;
	private static final int KAWA_TEHAI_AREA_TOIMEN_TOP = 0;

	private static final int KAWA_TEHAI_AREA_KAMICHA_LEFT = 392;
	private static final int KAWA_TEHAI_AREA_KAMICHA_TOP = 0;
	// private static final int KAWA_TEHAI_AREA_KAMICHA_LEFT = 235;
	// private static final int KAWA_TEHAI_AREA_KAMICHA_TOP = 47;

	private static final int KAWA_TEHAI_AREA_SHIMOCHA_LEFT = 0;
	private static final int KAWA_TEHAI_AREA_SHIMOCHA_TOP = 0;

	// private static final int KAWA_TEHAI_AREA_SHIMOCHA_LEFT = 0;
	// private static final int KAWA_TEHAI_AREA_SHIMOCHA_TOP = 38;

	private Bitmap getKawaTehaiAreaImage(Tehai tehai, Hou kawa, int place, int kaze, boolean isPlayer, Hai tsumoHai) {
		int width;
		int height;
		int kawaLeft = KAWA_LEFT;
		int kawaTop = KAWA_TOP;
		Bitmap image;
		Canvas canvas;

		switch (place) {
		case PLACE_PLAYER:
			width = 408;
			height = 94;
			if ((m_playerImage == null) || m_playerImage.isRecycled()) {
				m_playerImage = Bitmap.createBitmap(408, 94, Bitmap.Config.ARGB_4444);
			}
			image = m_playerImage;
			image.eraseColor(Color.TRANSPARENT);
			canvas = new Canvas(image);

			kawaLeft += 40;
			break;
		case PLACE_KAMICHA:
			width = KAWA_TEHAI_AREA_HEIGHT;
			height = KAWA_TEHAI_AREA_WIDTH;
			// m_kamichaImage = Bitmap.createBitmap(width, height,
			// Bitmap.Config.ARGB_8888);
			if ((m_kamichaImage == null) || m_kamichaImage.isRecycled()) {
				m_kamichaImage = Bitmap.createBitmap(KAWA_TEHAI_AREA_HEIGHT, KAWA_TEHAI_AREA_WIDTH, Bitmap.Config.ARGB_4444);
			}
			image = m_kamichaImage;
			image.eraseColor(Color.TRANSPARENT);
			canvas = new Canvas(image);
			canvas.rotate(270, 0, 0);
			canvas.translate(-height, 0);

			// kawaLeft += 6;
			break;
		case PLACE_TOIMEN:
			width = KAWA_TEHAI_AREA_WIDTH;
			height = KAWA_TEHAI_AREA_HEIGHT;
			// m_toimenImage = Bitmap.createBitmap(width, height,
			// Bitmap.Config.ARGB_8888);
			if ((m_toimenImage == null) || m_toimenImage.isRecycled()) {
				m_toimenImage = Bitmap.createBitmap(KAWA_TEHAI_AREA_WIDTH, KAWA_TEHAI_AREA_HEIGHT, Bitmap.Config.ARGB_4444);
			}
			image = m_toimenImage;
			image.eraseColor(Color.TRANSPARENT);
			canvas = new Canvas(image);
			canvas.rotate(180, 0, 0);
			canvas.translate(-width, -height);
			break;
		case PLACE_SHIMOCHA:
			width = KAWA_TEHAI_AREA_HEIGHT;
			height = KAWA_TEHAI_AREA_WIDTH;
			// m_shimochaImage = Bitmap.createBitmap(width, height,
			// Bitmap.Config.ARGB_8888);
			if ((m_shimochaImage == null) || m_shimochaImage.isRecycled()) {
				m_shimochaImage = Bitmap.createBitmap(KAWA_TEHAI_AREA_HEIGHT, KAWA_TEHAI_AREA_WIDTH, Bitmap.Config.ARGB_4444);
			}
			image = m_shimochaImage;
			image.eraseColor(Color.TRANSPARENT);
			canvas = new Canvas(image);
			canvas.rotate(90, 0, 0);
			canvas.translate(0, -width);
			break;
		default:
			return null;
		}

		image.setDensity(VIRTUAL_DENSITY);
		canvas.setDensity(VIRTUAL_DENSITY);

		drawHou(canvas, kawa, kawaLeft, kawaTop, null);

		if ((m_infoUi.getManKaze() == kaze)) {
			// if ((mInfoUi.getManKaze() == kaze) && (drawItem.tsumoKaze ==
			// kaze)) {
			drawPlayerTehai(canvas, tehai, tsumoHai, m_iSelectSutehai, TEHAI_LEFT, 50 - 4);
		} else {
			// m_drawItem.m_isDebug = true;
			drawTehai(canvas, tehai, tsumoHai, isPlayer | m_drawItem.m_isDebug, TEHAI_LEFT/* 26 */, TEHAI_TOP);
		}

		return image;
	}

	private void drawHou(Canvas a_canvas, Hou a_hou, int a_left, int a_top, Paint a_paint) {
		SuteHai[] suteHais = a_hou.getSuteHais();
		int suteHaisLength = a_hou.getSuteHaisLength();

		int left = a_left;
		int top;
		boolean reachFlag = false;
		int count = 0;
		int kiriCount = 12;

		for (int i = 0; i < suteHaisLength; i++) {
			if (count == kiriCount) {
				left = a_left;
				a_top += m_haiImageHeight;
				kiriCount += 12;
			}

			if (suteHais[i].isReach() || reachFlag) {
				if (!suteHais[i].isNaki()) {
					top = a_top + ((m_haiImageHeight - m_haiImageWidth) / 2);
					a_canvas.drawBitmap(getHorizontalHaiImage(suteHais[i]), left, top, a_paint);
					left += m_haiImageHeight;
					reachFlag = false;
					count++;
				} else {
					reachFlag = true;
				}
			} else {
				if (!suteHais[i].isNaki()) {
					a_canvas.drawBitmap(getHaiImage(suteHais[i]), left, a_top, a_paint);
					left += m_haiImageWidth;
					count++;
				}
			}
		}
	}

	private void drawTehai(Canvas a_canvas, Tehai a_tehai, Hai a_addHai, boolean a_disp, int a_left, int a_top) {
		Hai[] jyunTehai = a_tehai.getJyunTehai();
		int jyunTehaiLength = a_tehai.getJyunTehaiLength();

		int iSkip = m_drawItem.getISkip();

		int haiImageWidth = this.m_haiImageWidth;

		int left = a_left;
		int top = a_top + ((haiImageWidth * 2) - m_haiImageHeight);

		for (int i = 0; i < jyunTehaiLength; i++, left += haiImageWidth) {
			if ((a_addHai != null) && (m_drawItem.m_state == STATE_RIHAI_WAIT) && (i == iSkip)) {
				continue;
			}

			a_canvas.drawBitmap(a_disp ? getHaiImage(jyunTehai[i]) : m_hideHaiImage, left, top, null);
		}

		if (a_addHai != null) {
			left += TSUMO_OFFSET;
			a_canvas.drawBitmap(a_disp ? getHaiImage(a_addHai) : m_hideHaiImage, left, top, null);
		}

		drawFuuros(a_canvas, a_tehai, FUURO_LEFT, top);
	}

	private void drawFuuros(Canvas a_canvas, Tehai a_tehai, int a_left, int a_top) {
		int fuuroNums = a_tehai.getFuuroNum();
		if (fuuroNums > 0) {
			Fuuro[] fuuros = a_tehai.getFuuros();
			for (int i = 0; i < fuuroNums; i++) {
				Hai hais[] = fuuros[i].getHais();
				int type = fuuros[i].getType();

				if (type == Fuuro.TYPE_ANKAN) {
					a_left -= m_haiImageWidth;
					a_canvas.drawBitmap(mHaiUraImage, a_left, a_top, null);
					a_left -= m_haiImageWidth;
					a_canvas.drawBitmap(getHaiImage(hais[2]), a_left, a_top, null);
					a_left -= m_haiImageWidth;
					a_canvas.drawBitmap(getHaiImage(hais[1]), a_left, a_top, null);
					a_left -= m_haiImageWidth;
					a_canvas.drawBitmap(mHaiUraImage, a_left, a_top, null);
				} else {
					int relation = fuuros[i].getRelation();

					if (relation == Mahjong.RELATION_SHIMOCHA) {
						a_left -= m_haiImageHeight;
						a_canvas.drawBitmap(getHorizontalHaiImage(hais[2]), a_left, a_top + 4, null);
						if (type == Fuuro.TYPE_KAKAN) {
							a_canvas.drawBitmap(getHorizontalHaiImage(hais[3]), a_left, a_top - 15, null);
						} else if (type == Fuuro.TYPE_DAIMINKAN) {
							a_left -= m_haiImageWidth;
							a_canvas.drawBitmap(getHaiImage(hais[3]), a_left, a_top, null);
						}
						a_left -= m_haiImageWidth;
						a_canvas.drawBitmap(getHaiImage(hais[1]), a_left, a_top, null);
						a_left -= m_haiImageWidth;
						a_canvas.drawBitmap(getHaiImage(hais[0]), a_left, a_top, null);
					} else if (relation == Mahjong.RELATION_TOIMEN) {
						a_left -= m_haiImageWidth;
						a_canvas.drawBitmap(getHaiImage(hais[2]), a_left, a_top, null);
						if (type == Fuuro.TYPE_DAIMINKAN) {
							a_left -= m_haiImageWidth;
							a_canvas.drawBitmap(getHaiImage(hais[3]), a_left, a_top, null);
						}
						a_left -= m_haiImageHeight;
						a_canvas.drawBitmap(getHorizontalHaiImage(hais[1]), a_left, a_top + 4, null);
						if (type == Fuuro.TYPE_KAKAN) {
							a_canvas.drawBitmap(getHorizontalHaiImage(hais[3]), a_left, a_top - 15, null);
						}
						a_left -= m_haiImageWidth;
						a_canvas.drawBitmap(getHaiImage(hais[0]), a_left, a_top, null);
					} else {
						a_left -= m_haiImageWidth;
						a_canvas.drawBitmap(getHaiImage(hais[2]), a_left, a_top, null);
						a_left -= m_haiImageWidth;
						a_canvas.drawBitmap(getHaiImage(hais[1]), a_left, a_top, null);
						if (type == Fuuro.TYPE_DAIMINKAN) {
							a_left -= m_haiImageWidth;
							a_canvas.drawBitmap(getHaiImage(hais[3]), a_left, a_top, null);
						}
						a_left -= m_haiImageHeight;
						a_canvas.drawBitmap(getHorizontalHaiImage(hais[0]), a_left, a_top + 4, null);
						if (type == Fuuro.TYPE_KAKAN) {
							a_canvas.drawBitmap(getHorizontalHaiImage(hais[3]), a_left, a_top - 15, null);
						}
					}
				}
			}
		}
	}

	private void drawPlayerTehai(Canvas a_canvas, Tehai a_tehai, Hai a_addHai, int a_iSelect, int a_left, int a_top) {
		Hai[] jyunTehai = a_tehai.getJyunTehai();
		int jyunTehaiLength = a_tehai.getJyunTehaiLength();

		int iSkip = m_drawItem.getISkip();

		// Bitmap haiImage[] = this.m_largeHaiImage;
		int haiImageWidth = this.m_largeHaiImageWidth;

		int left = a_left;
		int topSelect = a_top - SELECT_OFFSET;
		int top = a_top;
		// int top = topSelect + SELECT_OFFSET;

		if (m_playerAction.getState() == PlayerAction.STATE_CHII_SELECT) {
			EventId chiiEventId = m_playerAction.getChiiEventId();
			Hai iSelects[];
			switch (chiiEventId) {
			case CHII_LEFT:
				iSelects = m_playerAction.getSarachiHaiLeft();
				break;
			case CHII_CENTER:
				iSelects = m_playerAction.getSarachiHaiCenter();
				break;
			default:
				iSelects = m_playerAction.getSarachiHaiRight();
				break;
			}
			int iSelect = 0;

			for (int i = 0; i < jyunTehaiLength; i++, left += haiImageWidth) {
				if ((a_addHai != null) && (m_drawItem.m_state == STATE_RIHAI_WAIT) && (i == iSkip)) {
					continue;
				}

				if ((iSelect < 2) && (jyunTehai[i].getId() == iSelects[iSelect].getId())) {
					// if ((jyunTehai[i].getId() == iSelects[0].getId()) ||
					// (jyunTehai[i].getId() == iSelects[1].getId())) {
					iSelect++;
					a_canvas.drawBitmap(getLargeHaiImage(jyunTehai[i]), left, topSelect, null);
				} else {
					a_canvas.drawBitmap(getLargeHaiImage(jyunTehai[i]), left, top, null);
				}
			}
		} else if (m_playerAction.getState() == PlayerAction.STATE_KAN_SELECT) {
			Hai[] kanHais = m_playerAction.getKanHais();
			// int kanNum = m_playerAction.getKanNum();
			int kanSelect = m_playerAction.getKanSelect();

			for (int i = 0; i < jyunTehaiLength; i++, left += haiImageWidth) {
				if ((a_addHai != null) && (m_drawItem.m_state == STATE_RIHAI_WAIT) && (i == iSkip)) {
					continue;
				}

				if (jyunTehai[i].getId() == kanHais[kanSelect].getId()) {
					a_canvas.drawBitmap(getLargeHaiImage(jyunTehai[i]), left, topSelect, null);
				} else {
					a_canvas.drawBitmap(getLargeHaiImage(jyunTehai[i]), left, top, null);
				}
			}

			if (a_addHai != null) {
				left += TSUMO_OFFSET;
				if ((a_addHai.getId() == kanHais[kanSelect].getId()) && (m_drawItem.m_state != STATE_RIHAI_WAIT) && (m_drawItem.m_state != STATE_RESULT)) {
					a_canvas.drawBitmap(getLargeHaiImage(a_addHai), left, topSelect, null);
				} else {
					a_canvas.drawBitmap(getLargeHaiImage(a_addHai), left, top, null);
				}
			}
		} else if (m_playerAction.getState() == PlayerAction.STATE_REACH_SELECT) {
			int[] indexs = m_playerAction.m_indexs;
			a_iSelect = indexs[m_playerAction.getReachSelect()];
			for (int i = 0; i < jyunTehaiLength; i++, left += haiImageWidth) {
				if ((a_addHai != null) && (m_drawItem.m_state == STATE_RIHAI_WAIT) && (i == iSkip)) {
					continue;
				}

				if (i == a_iSelect) {
					a_canvas.drawBitmap(getLargeHaiImage(jyunTehai[i]), left, topSelect, null);
				} else {
					a_canvas.drawBitmap(getLargeHaiImage(jyunTehai[i]), left, top, null);
				}
			}

			if (a_addHai != null) {
				left += TSUMO_OFFSET;
				if (a_iSelect >= jyunTehaiLength) {
					a_canvas.drawBitmap(getLargeHaiImage(a_addHai), left, topSelect, null);
				} else {
					a_canvas.drawBitmap(getLargeHaiImage(a_addHai), left, top, null);
				}
			}
		} else {
			for (int i = 0; i < jyunTehaiLength; i++, left += haiImageWidth) {
				if ((a_addHai != null) && (m_drawItem.m_state == STATE_RIHAI_WAIT) && (i == iSkip)) {
					continue;
				}

				if ((i == a_iSelect) && (m_playerAction.getState() == PlayerAction.STATE_SUTEHAI_SELECT)) {
					a_canvas.drawBitmap(getLargeHaiImage(jyunTehai[i]), left, topSelect, null);
				} else {
					a_canvas.drawBitmap(getLargeHaiImage(jyunTehai[i]), left, top, null);
				}
			}

			if (a_addHai != null) {
				left += TSUMO_OFFSET;
				if ((a_iSelect >= jyunTehaiLength) && (m_drawItem.m_state != STATE_RIHAI_WAIT) && (m_drawItem.m_state != STATE_RESULT)) {
					a_canvas.drawBitmap(getLargeHaiImage(a_addHai), left, topSelect, null);
				} else {
					a_canvas.drawBitmap(getLargeHaiImage(a_addHai), left, top, null);
				}
			}
		}

		drawFuuros(a_canvas, a_tehai, FUURO_LEFT + 32, top + 11);
	}

	private static final int TOUCH_TOP = 160;
	private static final int TOUCH_BOTTOM = 320;

	// private static final int TOUCH_TOP = 480 - 97;
	// private static final int TOUCH_BOTTOM = 480;

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (m_drawItem.m_state) {
		case STATE_END:
			if (ev.getAction() == MotionEvent.ACTION_DOWN) {
				m_game.setResult(Activity.RESULT_OK);
				m_game.finish();
				return true;
			}
			break;
		}

		if (m_drawItem.m_kazeFrom >= 4) {
			m_playerAction.actionNotifyAll();
			return true;
		}

		int x = (int) (ev.getX() * 480f / (float)getWidth());
		int y = (int) (ev.getY() * 320f / (float)getHeight());
		
		
		
		int action = ev.getAction();

		Log.d(TAG, "onTouchEvent: x = " + x + ", y = " + y);

		if (action == MotionEvent.ACTION_UP) {
			// return true;
		}

		boolean actionRequest = m_playerAction.isActionRequest();
		if (actionRequest) {
			if (action == MotionEvent.ACTION_DOWN) {
				if (!m_playerAction.isDispMenu()) {
					if ((160 <= y) && (y <= MESSAGE_AREA_BOTTOM)) {
						// if ((MESSAGE_AREA_TOP - MESSAGE_AREA_MARGIN <= y) &&
						// (y <= MESSAGE_AREA_BOTTOM)) {
						if ((MESSAGE_AREA_LEFT <= x) && (x <= MESSAGE_AREA_RIGHT)) {
							m_playerAction.setDispMenu(true);
							invalidate();
							return true;
						}
					}
					if (m_playerAction.getState() != PlayerAction.STATE_SUTEHAI_SELECT) {
						m_playerAction.actionNotifyAll();
						return true;
					}
				} else {
					int iMenu = 5;
					for (int i = 0; i < m_menuRect.length; i++) {
						if (x >= m_menuRect[i].left && x <= m_menuRect[i].right) {
							if (y >= 160 && y <= m_menuRect[i].bottom) {
								// if (y >= m_menuRect[i].top && y <=
								// m_menuRect[i].bottom) {
								iMenu = i;
								break;
							}
						}
					}
					if (iMenu >= m_playerAction.getMenuNum() && (m_playerAction.getState() != PlayerAction.STATE_RON_SELECT)) {
						m_playerAction.setDispMenu(false);
						invalidate();
						return true;
					}
					m_playerAction.setMenuSelect(iMenu);
					m_playerAction.actionNotifyAll();

					return true;
				}
			}
		}

		if (m_playerAction.getState() == PlayerAction.STATE_REACH_SELECT) {
			int indexNum = m_playerAction.m_indexNum;
			int reachSelect = m_playerAction.getReachSelect();
			// int[] indexs = m_playerAction.m_indexs;
			if (action == MotionEvent.ACTION_DOWN) {
				int ty = y;
				if ((TOUCH_TOP <= ty) && (ty <= TOUCH_BOTTOM)) {
					reachSelect++;
					if (reachSelect >= indexNum) {
						reachSelect = 0;
					}
					m_playerAction.setReachSelect(reachSelect);
					invalidate();
				} else {
					m_playerAction.actionNotifyAll();
				}
			}

			return true;
		}

		if (m_playerAction.getState() == PlayerAction.STATE_KAN_SELECT) {
			int kanNum = m_playerAction.getKanNum();
			int kanSelect = m_playerAction.getKanSelect();
			if (action == MotionEvent.ACTION_DOWN) {
				int ty = (int) y;
				if ((TOUCH_TOP <= ty) && (ty <= TOUCH_BOTTOM)) {
					kanSelect++;
					if (kanSelect >= kanNum) {
						kanSelect = 0;
					}
					m_playerAction.setKanSelect(kanSelect);
					invalidate();
				} else {
					m_playerAction.actionNotifyAll();
				}
			}

			return true;
		}

		if (m_playerAction.getState() == PlayerAction.STATE_CHII_SELECT) {
			boolean isValidChiiLeft = m_isValidChiiLeft;
			boolean isValidChiiCenter = m_isValidChiiCenter;
			boolean isValidChiiRight = m_isValidChiiRight;
			EventId chiiEventId = m_playerAction.getChiiEventId();
			if (action == MotionEvent.ACTION_DOWN) {
				// int tx = (int) event.getX();
				int ty = (int) y;
				if ((TOUCH_TOP <= ty) && (ty <= TOUCH_BOTTOM)) {
					switch (chiiEventId) {
					case CHII_LEFT:
						if (isValidChiiRight) {
							m_playerAction.setChiiEventId(EventId.CHII_RIGHT);
						} else {
							m_playerAction.setChiiEventId(EventId.CHII_CENTER);
						}
						break;
					case CHII_RIGHT:
						if (isValidChiiCenter) {
							m_playerAction.setChiiEventId(EventId.CHII_CENTER);
						} else {
							m_playerAction.setChiiEventId(EventId.CHII_LEFT);
						}
						break;
					default:
						if (isValidChiiLeft) {
							m_playerAction.setChiiEventId(EventId.CHII_LEFT);
						} else {
							m_playerAction.setChiiEventId(EventId.CHII_RIGHT);
						}
						break;
					}
					invalidate();
				} else {
					m_isValidChiiLeft = m_isValidChiiCenter = m_isValidChiiRight = false;
					m_playerAction.actionNotifyAll();
				}
			}
			return true;
		}

		if (action == MotionEvent.ACTION_DOWN) {
			synchronized (m_drawItem) {
				switch (m_drawItem.m_state) {
				case STATE_PLAY:
					break;
				default:
					m_playerAction.actionNotifyAll();
					return true;
				}
			}
		}

		PlayerInfo playerInfo;
		try {
			synchronized (m_drawItem) {
				playerInfo = m_drawItem.m_playerInfos[m_drawItem.m_kazeFrom];
			}
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			return true;
		}
		int jyunTehaiLength = playerInfo.m_tehai.getJyunTehaiLength();
		if (playerInfo.m_tsumoHai != null) {
			jyunTehaiLength++;
		}
		jyunTehaiLength--;

		/* X,Y座標の取得 */
		int tx = (int) x;
		int ty = (int) y;
		int act_evt = ev.getAction();

		/* Y座標の判定(牌の高さの間) */
		if ((TOUCH_TOP <= ty) && (ty <= TOUCH_BOTTOM)) {
			if (m_drawItem.m_isManReach) {
				m_iSelectSutehai = jyunTehaiLength;
			} else {
				int iSelect = (tx - 46) / m_largeHaiImageWidth;
				if (iSelect > jyunTehaiLength) {
					iSelect = jyunTehaiLength;
				} else if (iSelect < 0) {
					iSelect = 0;
				}
				if (m_iSelectSutehai == iSelect) {
					return true;
				}
				m_iSelectSutehai = iSelect;
				invalidate(0, 240, 480, 320);
				return true;
			}
		} else {
			if (act_evt == MotionEvent.ACTION_DOWN) {
				synchronized (m_drawItem) {
					switch (m_drawItem.m_state) {
					case STATE_PLAY:
						m_playerAction.setSutehaiIdx(m_iSelectSutehai);
						m_playerAction.actionNotifyAll();
						break;
					default:
						m_playerAction.actionNotifyAll();
						break;
					}
				}
			}
		}
		/* 再描画の指示 */
		invalidate();
		return true;
	}

	private int m_iSelectSutehai = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(TAG, "onKeyDown: keycode=" + keyCode + ", event=" + event);
		if (m_drawItem.m_state == STATE_END) {
			return super.onKeyDown(keyCode, event);
		}

		if (m_drawItem.m_kazeFrom >= 4) {
			m_playerAction.actionNotifyAll();
			return true;
		}

		boolean actionRequest = m_playerAction.isActionRequest();
		int menuSelect = m_playerAction.getMenuSelect();
		if (actionRequest) {
			if (!m_playerAction.isDispMenu()) {
				// m_playerAction.setDispMenu(true);
			} else {
				switch (keyCode) {
				case KeyEvent.KEYCODE_DPAD_LEFT:
					menuSelect--;
					menuSelect %= 2;
					m_playerAction.setMenuSelect(menuSelect);
					break;
				case KeyEvent.KEYCODE_DPAD_RIGHT:
					menuSelect++;
					menuSelect %= 2;
					m_playerAction.setMenuSelect(menuSelect);
					break;
				case KeyEvent.KEYCODE_ENTER:
				case KeyEvent.KEYCODE_DPAD_CENTER:
					m_playerAction.actionNotifyAll();
					break;
				default:
					return super.onKeyDown(keyCode, event);
				}

				invalidate();
				return true;
			}
		}

		int state = m_playerAction.getState();
		if (state == PlayerAction.STATE_REACH_SELECT) {
			int indexNum = m_playerAction.m_indexNum;
			int reachSelect = m_playerAction.getReachSelect();
			// int[] indexs = m_playerAction.m_indexs;
			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_LEFT:
				reachSelect--;
				if (reachSelect < 0) {
					reachSelect = indexNum - 1;
				}
				m_playerAction.setReachSelect(reachSelect);
				invalidate();
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				reachSelect++;
				if (reachSelect >= indexNum) {
					reachSelect = 0;
				}
				m_playerAction.setReachSelect(reachSelect);
				invalidate();
				break;
			default:
				// return super.onKeyDown(keyCode, event);
			}
		}
		if (state == PlayerAction.STATE_KAN_SELECT) {
			int kanNum = m_playerAction.getKanNum();
			int kanSelect = m_playerAction.getKanSelect();
			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_LEFT:
				kanSelect--;
				if (kanSelect < 0) {
					kanSelect = kanNum - 1;
				}
				m_playerAction.setKanSelect(kanSelect);
				invalidate();
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				kanSelect++;
				if (kanSelect >= kanNum) {
					kanSelect = 0;
				}
				m_playerAction.setKanSelect(kanSelect);
				invalidate();
				break;
			default:
				// return super.onKeyDown(keyCode, event);
			}
		}
		if (state == PlayerAction.STATE_CHII_SELECT) {
			boolean isValidChiiLeft = m_isValidChiiLeft;
			boolean isValidChiiCenter = m_isValidChiiCenter;
			boolean isValidChiiRight = m_isValidChiiRight;
			EventId chiiEventId = m_playerAction.getChiiEventId();
			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_LEFT:
				switch (chiiEventId) {
				case CHII_RIGHT:
					if (isValidChiiLeft) {
						m_playerAction.setChiiEventId(EventId.CHII_LEFT);
					} else {
						m_playerAction.setChiiEventId(EventId.CHII_CENTER);
					}
					break;
				case CHII_CENTER:
					if (isValidChiiRight) {
						m_playerAction.setChiiEventId(EventId.CHII_RIGHT);
					} else {
						m_playerAction.setChiiEventId(EventId.CHII_LEFT);
					}
					break;
				default:
					if (isValidChiiCenter) {
						m_playerAction.setChiiEventId(EventId.CHII_CENTER);
					} else {
						m_playerAction.setChiiEventId(EventId.CHII_RIGHT);
					}
					break;
				}
				invalidate();
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				switch (chiiEventId) {
				case CHII_LEFT:
					if (isValidChiiRight) {
						m_playerAction.setChiiEventId(EventId.CHII_RIGHT);
					} else {
						m_playerAction.setChiiEventId(EventId.CHII_CENTER);
					}
					break;
				case CHII_RIGHT:
					if (isValidChiiCenter) {
						m_playerAction.setChiiEventId(EventId.CHII_CENTER);
					} else {
						m_playerAction.setChiiEventId(EventId.CHII_LEFT);
					}
					break;
				default:
					if (isValidChiiLeft) {
						m_playerAction.setChiiEventId(EventId.CHII_LEFT);
					} else {
						m_playerAction.setChiiEventId(EventId.CHII_RIGHT);
					}
					break;
				}
				invalidate();
				break;
			default:
				// return super.onKeyDown(keyCode, event);
			}
			// invalidate();
			// return true;
		}

		PlayerInfo playerInfo = m_drawItem.m_playerInfos[m_drawItem.m_kazeFrom];
		int jyunTehaiLength = playerInfo.m_tehai.getJyunTehaiLength();
		if (playerInfo.m_tsumoHai != null) {
			jyunTehaiLength++;
		}
		jyunTehaiLength--;

		// int state = m_playerAction.getState();
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_UP:
			if (state != PlayerAction.STATE_SUTEHAI_SELECT) {
				return true;
			}
			// mSelectSutehaiIdx = 0;
			if (m_drawItem.m_isDebug) {
				// m_drawItem.m_isDebug = false;
			} else {
				// m_drawItem.m_isDebug = true;
			}
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			if (state != PlayerAction.STATE_SUTEHAI_SELECT) {
				return true;
			}
			// mSelectSutehaiIdx = 100;
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if (state != PlayerAction.STATE_SUTEHAI_SELECT) {
				return true;
			}
			if (m_iSelectSutehai > jyunTehaiLength) {
				m_iSelectSutehai = jyunTehaiLength;
			}
			m_iSelectSutehai--;
			if (m_iSelectSutehai < 0) {
				m_iSelectSutehai = jyunTehaiLength;
			}
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if (state != PlayerAction.STATE_SUTEHAI_SELECT) {
				return true;
			}
			m_iSelectSutehai++;
			if (m_iSelectSutehai > jyunTehaiLength) {
				m_iSelectSutehai = 0;
			}
			break;
		case KeyEvent.KEYCODE_ENTER:
		case KeyEvent.KEYCODE_DPAD_CENTER:
			m_isValidChiiLeft = m_isValidChiiCenter = m_isValidChiiRight = false;
			synchronized (m_drawItem) {
				switch (m_drawItem.m_state) {
				case STATE_PLAY:
					m_playerAction.setSutehaiIdx(m_iSelectSutehai);
					m_playerAction.actionNotifyAll();
					break;
				default:
					m_playerAction.actionNotifyAll();
					break;
				}
			}

			break;
		default:
			return super.onKeyDown(keyCode, event);
		}
		if (m_drawItem.m_isManReach) {
			m_iSelectSutehai = jyunTehaiLength;
		}
		invalidate();
		return true;
	}

	/** 捨牌のインデックス */
	private int mSutehaiIdx = 0;

	public static int DELAY_MS = 500;

	public String getName() {
		return mName;
	}

	// private int mSkipIdx = 0;

	/** 進行の待ち時間 */
	// private static int PROGRESS_WAIT_TIME = 100;
	private static int PROGRESS_WAIT_TIME = 20;

	/**
	 * イベントを処理する。
	 * 
	 * @param a_kazeFrom
	 *            イベントを発行した家
	 * @param a_kazeTo
	 *            イベントの対象の家
	 * @param a_eventId
	 *            イベントID
	 */
	public EventId event(EventId a_eventId, int a_kazeFrom, int a_kazeTo) {
		m_drawItem.m_eventId = a_eventId;
		m_drawItem.m_kazeFrom = a_kazeFrom;
		m_drawItem.m_kazeTo = a_kazeTo;

		Log.d("UI", "a_eventId = " + a_eventId.toString() + ", a_kazeFrom = " + a_kazeFrom + ", a_kazeTo = " + a_kazeTo);
		switch (a_eventId) {
		case UI_WAIT_PROGRESS:// 進行待ち
			try {
				Thread.sleep(PROGRESS_WAIT_TIME, 0);
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			break;
		case START_GAME:// 親決め
			// この段階で初期化されている情報を設定する。
			m_drawItem.setState(STATE_NONE);
			synchronized (m_drawItem) {
				// 点棒を設定する。
				for (int i = 0; i < m_drawItem.m_playerInfos.length; i++) {
					m_drawItem.m_playerInfos[i].m_tenbo = m_infoUi.getTenbou(i);
				}

				// リーチ棒の数を設定する。
				m_drawItem.setReachbou(m_infoUi.getReachbou());

				// 本場を設定する。
				m_drawItem.setHonba(m_infoUi.getHonba());

				// 起家を設定する。
				m_drawItem.setChiicha(m_infoUi.getChiichaIdx());

				m_drawItem.m_tsumoRemain = 0;
			}
			break;
		case END_GAME:
			synchronized (m_drawItem) {
				for (int i = 0; i < m_drawItem.m_playerInfos.length; i++) {
					m_drawItem.m_playerInfos[i].m_tenbo = m_infoUi.getTenbou(i);
				}
				m_drawItem.setHonba(m_infoUi.getHonba());
				m_drawItem.setReachbou(m_infoUi.getReachbou());
			}
			m_drawItem.setState(STATE_END);
			this.postInvalidate(0, 0, getWidth(), getHeight());
			break;
		case START_KYOKU:// サイ振り
			// サイ振りを局の開始と考える。
			m_drawItem.m_isManReach = false;

			// 局の文字列を設定する。
			m_drawItem.setKyokuString(getResources(), m_infoUi.getkyoku());

			m_drawItem.setState(STATE_KYOKU_START);

			// 描画する。
			this.postInvalidate(0, 0, getWidth(), getHeight());

			// アクションを待つ。
			m_playerAction.actionWait();
			// break;
			// case HAIPAI:
			// 手牌を設定して、プレイ状態にする。
			m_drawItem.setState(STATE_PLAY);
			synchronized (m_drawItem) {
				for (int i = 0; i < m_drawItem.m_playerInfos.length; i++) {
					m_infoUi.copyTehai(m_drawItem.m_playerInfos[i].m_tehai, i);
					m_infoUi.copyKawa(m_drawItem.m_playerInfos[i].m_kawa, i);
					m_drawItem.m_playerInfos[i].m_tsumoHai = null;
					m_drawItem.m_playerInfos[i].m_tenbo = m_infoUi.getTenbou(i);
				}
				m_drawItem.setHonba(m_infoUi.getHonba());
				m_drawItem.setReachbou(m_infoUi.getReachbou());
				m_drawItem.m_tsumoRemain = m_infoUi.getTsumoRemain();
			}

			// 描画する。
			this.postInvalidate(0, 0, getWidth(), getHeight());
			break;
		case RYUUKYOKU:// 流局
			boolean tenpai[] = m_infoUi.getTenpai();
			synchronized (m_drawItem) {
				for (int i = 0; i < m_drawItem.m_playerInfos.length; i++) {
					m_drawItem.m_playerInfos[i].m_tenpai = tenpai[i];
					m_drawItem.m_playerInfos[i].m_tenbo = m_infoUi.getTenbou(i);
				}
			}

			// サイ振りを局の開始と考える。
			m_drawItem.setState(STATE_RYUUKYOKU);

			// 描画する。
			this.postInvalidate(0, 0, getWidth(), getHeight());

			// アクションを待つ。
			m_playerAction.actionWait();

			synchronized (m_drawItem) {
				for (int i = 0; i < m_drawItem.m_playerInfos.length; i++) {
					m_drawItem.m_playerInfos[i].m_tenpai = false;
				}
			}
			break;
		case NAGASHI:// 流し
			// 何も表示しない。
			break;
		case TSUMO:// ツモ
			synchronized (m_drawItem) {
				// ツモ牌を取得する。
				m_drawItem.m_playerInfos[m_infoUi.getJikaze()].m_tsumoHai = m_infoUi.getTsumoHai();

				m_drawItem.m_tsumoRemain = m_infoUi.getTsumoRemain();
			}

			// 描画する。
			this.postInvalidate(0, 0, getWidth(), getHeight());
			delay();
			break;
		case TSUMO_AGARI:// ツモあがり
			m_drawItem.setState(STATE_TSUMO);
			m_infoUi.copyTehai(m_drawItem.m_playerInfos[a_kazeFrom].m_tehai, a_kazeFrom);
			m_drawItem.m_playerInfos[a_kazeFrom].m_tsumoHai = m_infoUi.getTsumoHai();
			m_drawItem.m_playerInfos[a_kazeFrom].m_tenpai = true;

			// 描画する。
			this.postInvalidate(0, 0, getWidth(), getHeight());

			// アクションを待つ。
			m_playerAction.actionWait();

			// 結果画面を表示する。
			m_drawItem.m_state = STATE_RESULT;

			m_drawItem.m_playerInfos[a_kazeFrom].m_tenpai = false;

			// 描画する。
			this.postInvalidate(0, 0, getWidth(), getHeight());

			// アクションを待つ。
			m_playerAction.actionWait();
			break;
		case SUTEHAI:// 捨牌
			// 手牌をコピーする。
			m_infoUi.copyTehai(m_drawItem.m_playerInfos[a_kazeFrom].m_tehai, a_kazeFrom);

			// 河をコピーする。
			m_infoUi.copyKawa(m_drawItem.m_playerInfos[a_kazeFrom].m_kawa, a_kazeFrom);

			// ツモ牌をなくす。
			m_drawItem.m_playerInfos[a_kazeFrom].m_tsumoHai = null;

			// 描画する。
			this.postInvalidate(0, 0, getWidth(), getHeight());
			delay();
			break;
		case SELECT_SUTEHAI:
			if (a_kazeFrom == m_infoUi.getJikaze()) {
				for (int i = 0; i < m_drawItem.m_playerInfos.length; i++) {
					m_infoUi.copyTehai(m_drawItem.m_playerInfos[i].m_tehai, i);
				}
				// mDrawItem.tsumoKaze = 5;
				// mDrawItem.tsumoHai = null;
				// mDrawItem.tsumoHais[mInfoUi.getJikaze()] = null;
				this.postInvalidate(0, 0, getWidth(), getHeight());
			}
			delay();
			break;
		case UI_WAIT_RIHAI:
			m_drawItem.setSkipIdx(m_infoUi.getSutehaiIdx());
			m_drawItem.m_state = STATE_RIHAI_WAIT;
			this.postInvalidate(0, 0, getWidth(), getHeight());
			try {
				Thread.sleep(PROGRESS_WAIT_TIME, 0);
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			m_iSelectSutehai = 13;
			m_drawItem.m_state = STATE_PLAY;
			break;
		case PON:// ポン
		case CHII_LEFT:
		case CHII_CENTER:
		case CHII_RIGHT:
		case KAN:
		case ANKAN:
		case DAIMINKAN:
			// 自分の捨牌のみを表示します。
			if (a_kazeFrom == m_infoUi.getJikaze()) {
				{
					for (int i = 0; i < m_drawItem.m_playerInfos.length; i++) {
						m_infoUi.copyTehai(m_drawItem.m_playerInfos[i].m_tehai, i);
						m_infoUi.copyKawa(m_drawItem.m_playerInfos[i].m_kawa, i);
					}
					// mDrawItem.tsumoKaze = 5;
					// mDrawItem.tsumoHai = null;
					// m_drawItem.m_playerInfos[m_infoUi.getJikaze()] = null;
					m_iSelectSutehai = 0;
					this.postInvalidate(0, 0, getWidth(), getHeight());
				}
			}
			break;
		case REACH:
			synchronized (m_drawItem) {
				for (int i = 0; i < m_drawItem.m_playerInfos.length; i++) {
					m_drawItem.m_playerInfos[i].m_tenbo = m_infoUi.getTenbou(i);
				}
				m_drawItem.setReachbou(m_infoUi.getReachbou());
			}

			// 手牌をコピーする。
			m_infoUi.copyTehai(m_drawItem.m_playerInfos[a_kazeFrom].m_tehai, a_kazeFrom);

			// 河をコピーする。
			m_infoUi.copyKawa(m_drawItem.m_playerInfos[a_kazeFrom].m_kawa, a_kazeFrom);

			// ツモ牌をなくす。
			m_drawItem.m_playerInfos[a_kazeFrom].m_tsumoHai = null;

			if (m_infoUi.getManKaze() == a_kazeFrom) {
				m_drawItem.m_isManReach = true;
				// mSelectSutehaiIdx = m_playerAction.m_indexs[0];
			}
			m_drawItem.m_state = STATE_REACH;
			this.postInvalidate(0, 0, getWidth(), getHeight());
			delay();
			delay();
			m_drawItem.m_state = STATE_PLAY;
			// 描画する。
			this.postInvalidate(0, 0, getWidth(), getHeight());
			break;
		case RON_AGARI:// ロン
			// 手牌をコピーする。
			m_infoUi.copyTehai(m_drawItem.m_playerInfos[a_kazeFrom].m_tehai, a_kazeFrom);
			m_drawItem.m_suteHai = m_infoUi.getSuteHai();
			m_drawItem.m_playerInfos[a_kazeFrom].m_tenpai = true;

			m_drawItem.m_state = STATE_RON;
			this.postInvalidate(0, 0, getWidth(), getHeight());

			// アクションを待つ。
			m_playerAction.actionWait();

			// 結果画面を表示する。
			m_drawItem.m_state = STATE_RESULT;

			m_drawItem.m_playerInfos[a_kazeFrom].m_tenpai = false;

			// 描画する。
			this.postInvalidate(0, 0, getWidth(), getHeight());

			// アクションを待つ。
			m_playerAction.actionWait();

		case UI_INPUT_PLAYER_ACTION:
			this.postInvalidate(0, 0, getWidth(), getHeight());
			break;
		default:
			break;
		}

		return EventId.NAGASHI;
	}

	void delay() {
		try {
			Thread.sleep(DELAY_MS);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public int getISutehai() {
		return mSutehaiIdx;
	}
}