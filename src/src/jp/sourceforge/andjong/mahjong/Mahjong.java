package jp.sourceforge.andjong.mahjong;

import android.util.Log;
import jp.sourceforge.andjong.AndjongView;
import jp.sourceforge.andjong.mahjong.AgariScore;
import jp.sourceforge.andjong.mahjong.AgariScore.AgariInfo;
import jp.sourceforge.andjong.mahjong.AgariSetting.YakuflgName;
import jp.sourceforge.andjong.mahjong.CountFormat.Combi;
import static jp.sourceforge.andjong.mahjong.EventIf.*;

/**
 * ゲームを管理するクラスです。
 *
 * @author Yuji Urushibara
 *
 */
public class Mahjong implements Runnable {
	private static final String TAG = "Mahjong";

	/** AndjongView */
	private AndjongView m_view;

	/** 山 */
	private Yama m_yama;

	/** 東一局 */
	public final static int KYOKU_TON_1 = 0;
	/** 東二局 */
	public final static int KYOKU_TON_2 = 1;
	/** 東三局 */
	public final static int KYOKU_TON_3 = 2;
	/** 東四局 */
	public final static int KYOKU_TON_4 = 3;
	/** 南一局 */
	public final static int KYOKU_NAN_1 = 4;
	/** 南二局 */
	public final static int KYOKU_NAN_2 = 5;
	/** 南三局 */
	public final static int KYOKU_NAN_3 = 6;
	/** 南四局 */
	public final static int KYOKU_NAN_4 = 7;
	
	/** 局 */
	private int m_kyoku;

	/** 局の最大値 */
	private int m_kyokuEnd;

	/** ツモ牌 */
	private Hai m_tsumoHai;

	/** 捨牌 */
	private Hai m_suteHai;

	/** リーチ棒の数 */
	private int m_reachbou;

	/** 本場 */
	private int m_honba;

	/** プレイヤーの人数 */
	private int m_playerNum;

	/** プレイヤーに提供する情報 */
	private Info m_info;

	/** プレイヤーの配列 */
	private Player[] m_players;

	/** 風をプレイヤーインデックスに変換する配列 */
	private int[] m_kazeToPlayerIdx = new int[4];

	/** UIに提供する情報 */
	private InfoUi m_infoUi;

	/** サイコロの配列 */
	private Sai[] m_sais = new Sai[] { new Sai(), new Sai() };

	/** 親のプレイヤーインデックス */
	private int m_iOya;

	/** 起家のプレイヤーインデックス */
	private int m_iChiicha;

	/** 連荘 */
	private boolean m_renchan;

	/** イベントを発行した風 */
	private int m_kazeFrom;

	/** イベントの対象となった風 */
	private int m_kazeTo;

	/** 持ち点の初期値 */
	private static final int TENBOU_INIT = 25000;

	private int m_suteHaisCount = 0;
	public int getSuteHaisCount() {
		return m_suteHaisCount;
	}
	private SuteHai[] m_suteHais = new SuteHai[136];
	public SuteHai[] getSuteHais() {
		return m_suteHais;
	}

	public int getPlayerSuteHaisCount(int a_kaze) {
		return m_players[m_kazeToPlayerIdx[a_kaze]].getSuteHaisCount();
	}

	
	/**
	 * コンストラクタ
	 *
	 * @param view
	 *            View
	 */
	public Mahjong(AndjongView view) {
		super();
		this.m_view = view;
	}

	/**
	 * 山を取得する。
	 *
	 * @return 山
	 */
	Yama getYama() {
		return m_yama;
	}

	/**
	 * 局を取得する。
	 *
	 * @return 局
	 */
	int getkyoku() {
		return m_kyoku;
	}

	/**
	 * ツモ牌を取得する。
	 *
	 * @return ツモ牌
	 */
	Hai getTsumoHai() {
		return m_tsumoHai;
	}

	/**
	 * 捨牌を取得する。
	 *
	 * @return 捨牌
	 */
	Hai getSuteHai() {
		return m_suteHai;
	}

	public int getReachbou() {
		return m_reachbou;
	}

	public void setReachbou(int reachbou) {
		m_reachbou = reachbou;
	}

	/**
	 * 起家のプレイヤーインデックスを取得する。
	 *
	 * @return 起家のプレイヤーインデックス
	 */
	public int getChiichaIdx() {
		return m_iChiicha;
	}

	/**
	 * サイコロの配列を取得する。
	 *
	 * @return サイコロの配列
	 */
	Sai[] getSais() {
		return m_sais;
	}

	public int getRelation(
			int fromKaze,
			int toKaze) {
		int relation;
		if (fromKaze == toKaze) {
			relation = RELATION_JIBUN;
		} else if ((fromKaze + 1) % 4 == toKaze) {
			relation = RELATION_SHIMOCHA;
		} else if ((fromKaze + 2) % 4 == toKaze) {
			relation = RELATION_TOIMEN;
		} else {
			relation = RELATION_KAMICHA;
		}
		return relation;
	}

	/*
	 * 共通定義
	 */

	/** 面子の構成牌の数(3個) */
	public static final int MENTSU_HAI_MEMBERS_3 = 3;
	/** 面子の構成牌の数(4個) */
	public static final int MENTSU_HAI_MEMBERS_4 = 4;

	/** 他家との関係 自分 */
	public static final int RELATION_JIBUN = 0;
	/** 他家との関係 上家 */
	public static final int RELATION_KAMICHA = 1;
	/** 他家との関係 対面 */
	public static final int RELATION_TOIMEN = 2;
	/** 他家との関係 下家 */
	public static final int RELATION_SHIMOCHA = 3;

	/** 割れ目 */
	//private int mWareme;

	/** アクティブプレイヤー */
	private Player activePlayer;

	private PlayerAction m_playerAction = new PlayerAction();

	public int getManKaze() {
		return m_players[0].getJikaze();
	}

	/**
	 * ゲームを開始する。
	 */
	public void play() {
		// 初期化する。
		initialize();

		// 親を決める。
		m_sais[0].saifuri();
		m_sais[1].saifuri();
		m_iOya = (m_sais[0].getNo() + m_sais[1].getNo() - 1) % 4;
		m_iChiicha = m_iOya;

		// イベント（親決め）を発行する。
		//m_view.event(EventId.OYAGIME, KAZE_NONE, KAZE_NONE);
		m_view.event(EventId.START_GAME, KAZE_NONE, KAZE_NONE);

		// 局を繰り返して、ゲームを進行する。
		while (m_kyoku <= m_kyokuEnd) {
			// 局を開始する。
			startKyoku();

			// 連荘の場合、局を進めない。
			if (m_renchan) {
				// イベント（連荘）を発行する。
				//m_view.event(EventId.RENCHAN, KAZE_NONE, KAZE_NONE);
				continue;
			}

			// 局を進める。
			m_kyoku++;
		}

		// イベント（ゲームの終了）を発行する。
		m_view.event(EventId.END_GAME, KAZE_NONE, KAZE_NONE);
	}

	/**
	 * 初期化する。
	 */
	private void initialize() {
		// 山を作成する。
		m_yama = new Yama();

		// 赤ドラを設定する。
		if (m_view.isAkaDora()) {
			m_yama.setRedDora(Hai.ID_PIN_5, 2);
			m_yama.setRedDora(Hai.ID_WAN_5, 1);
			m_yama.setRedDora(Hai.ID_SOU_5, 1);
		}

		// 局を初期化する。
		m_kyoku = KYOKU_TON_1;
		//m_kyoku = KYOKU_TON_4;

		// 局の終了を設定する。
		//m_kyokuEnd = KYOKU_NAN_4;
		m_kyokuEnd = m_view.getKyokusuu();

		// ツモ牌を作成する。
		m_tsumoHai = new Hai();

		// 捨牌を作成する。
		m_suteHai = new Hai();

		m_suteHaisCount = 0;

		// リーチ棒の数を初期化する。
		m_reachbou = 0;

		// 本場を初期化する。
		m_honba = 0;

		// プレイヤーの人数を設定する。
		m_playerNum = 4;

		// プレイヤーに提供する情報を作成する。
		m_info = new Info(this);

		// プレイヤーの配列を初期化する。
		m_players = new Player[m_playerNum];
		m_players[0] = new Player((EventIf) new Man(m_info, "A", m_playerAction));
		m_players[1] = new Player((EventIf) new AI(m_info, "B"));
		m_players[2] = new Player((EventIf) new AI(m_info, "C"));
		m_players[3] = new Player((EventIf) new AI(m_info, "D"));

		for (int i = 0; i < m_playerNum; i++) {
			m_players[i].setTenbou(TENBOU_INIT);
		}

		// 風をプレイヤーインデックスに変換する配列を初期化する。
		m_kazeToPlayerIdx = new int[m_players.length];

		// UIに提供する情報を作成する。
		m_infoUi = new InfoUi(this, m_playerAction);

		// UIを初期化する。
		m_view.initUi(m_infoUi, "AndjongView");
	}

	boolean m_tenpai[] = new boolean[4];

	public boolean[] getTenpai() {
		return m_tenpai;
	}

	/**
	 * 局を開始する。
	 */
	private void startKyoku() {
		// 連荘を初期化する。
		m_renchan = false;

		m_isTenhou = true;
		m_isChiihou = true;
		m_isTsumo = false;
		m_isRinshan = false;
		m_isLast = false;

		// プレイヤーの自風を設定する。
		setJikaze();

		// イベントを発行した風を初期化する。
		m_kazeFrom = m_players[m_iOya].getJikaze();

		// イベントの対象となった風を初期化する。
		m_kazeTo = m_players[m_iOya].getJikaze();

		// プレイヤー配列を初期化する。
		for (int i = 0; i < m_players.length; i++) {
			m_players[i].init();
		}

		m_suteHaisCount = 0;

		// 洗牌する。
		m_yama.xipai();

		// サイ振りをする。
		m_sais[0].saifuri();
		m_sais[1].saifuri();

		// UIイベント（サイ振り）を発行する。
		//m_view.event(EventId.SAIFURI, mFromKaze, mToKaze);

		// 山に割れ目を設定する。
		setWareme(m_sais);

		// 配牌する。
		haipai();

		// UIイベント（配牌）を発行する。
		//m_view.event(EventId.HAIPAI, mFromKaze, mToKaze);
		
		m_view.event(EventId.START_KYOKU, m_kazeFrom, m_kazeTo);

		EventId retEid;

		int tsumoNokori;
		int score;
		int iPlayer;
		// 局を進行する。
		KYOKU_MAIN: while (true) {
			// UIイベント（進行待ち）を発行する。
			m_view.event(EventId.UI_WAIT_PROGRESS, KAZE_NONE, KAZE_NONE);

			// ツモ牌を取得する。
			m_tsumoHai = m_yama.tsumo();

			// ツモ牌がない場合、流局する。
			if (m_tsumoHai == null) {
				// 流し満貫の確認をする。
				for (int i = 0, j = m_iOya; i < m_players.length; i++, j++) {
					if (j >= m_players.length) {
						j = 0;
					}

					boolean agari = true;
					Hou hou = m_players[j].getKawa();
					SuteHai[] suteHais = hou.getSuteHais();
					int suteHaisLength = hou.getSuteHaisLength();
					for (int k = 0; k < suteHaisLength; k++) {
						if (suteHais[k].isNaki() || !suteHais[k].isYaochuu()) {
							agari = false;
							break;
						}
					}

					if (agari) {
						m_kazeFrom = m_kazeTo = m_players[j].getJikaze();

						m_score = new AgariScore();
						m_score.setNagashiMangan(m_agariInfo, m_view.getResources());

						iPlayer = m_kazeToPlayerIdx[m_kazeFrom];
						if (m_iOya == iPlayer) {
							score = m_agariInfo.m_score.m_oyaRon + (m_honba * 300);
							for (int l = 0; l < 3; l++) {
								iPlayer = (iPlayer + 1) % 4;
								m_players[iPlayer].reduceTenbou(m_agariInfo.m_score.m_oyaTsumo + (m_honba * 100));
							}
						} else {
							score = m_agariInfo.m_score.m_koRon + (m_honba * 300);
							for (int l = 0; l < 3; l++) {
								iPlayer = (iPlayer + 1) % 4;
								if (m_iOya == iPlayer) {
									m_players[iPlayer].reduceTenbou(m_agariInfo.m_score.m_oyaTsumo + (m_honba * 100));
								} else {
									m_players[iPlayer].reduceTenbou(m_agariInfo.m_score.m_koTsumo + (m_honba * 100));
								}
							}
						}

						activePlayer.increaseTenbou(score);
						m_agariInfo.m_agariScore = score - (m_honba * 300);

						// 点数を清算する。
						activePlayer.increaseTenbou(m_reachbou * 1000);

						// リーチ棒の数を初期化する。
						m_reachbou = 0;

						// UIイベント（ツモあがり）を発行する。
						m_view.event(EventId.TSUMO_AGARI, m_kazeFrom, m_kazeTo);

						// 親を更新する。
						if (m_iOya != m_kazeToPlayerIdx[m_kazeFrom]) {
							m_iOya++;
							if (m_iOya >= m_players.length) {
								m_iOya = 0;
							}
							m_honba = 0;
						} else {
							m_renchan = true;
							m_honba++;
						}

						break KYOKU_MAIN;
					}
				}

				// テンパイの確認をする。
				int tenpaiCount = 0;
				for (int i = 0; i < m_tenpai.length; i++) {
					iPlayer = m_kazeToPlayerIdx[i];
					m_tenpai[i] = m_players[iPlayer].isTenpai();
					if (m_tenpai[i]) {
						tenpaiCount++;
					}
				}
				int incScore = 0;
				int redScore = 0;
				switch (tenpaiCount) {
				case 0:
					break;
				case 1:
					incScore = 3000;
					redScore = 1000;
					break;
				case 2:
					incScore = 1500;
					redScore = 1500;
					break;
				case 3:
					incScore = 1000;
					redScore = 3000;
					break;
				}
				for (int i = 0; i < m_tenpai.length; i++) {
					if (m_tenpai[i]) {
						m_players[m_kazeToPlayerIdx[i]].increaseTenbou(incScore);
					} else {
						m_players[m_kazeToPlayerIdx[i]].reduceTenbou(redScore);
					}
				}

				// UIイベント（流局）を発行する。
				m_view.event(EventId.RYUUKYOKU, KAZE_NONE, KAZE_NONE);

				// フラグを落としておく。
				for (int i = 0; i < m_tenpai.length; i++) {
					m_tenpai[i] = false;
				}

				// 親を更新する。上がり連荘とする。
				m_iOya++;
				if (m_iOya >= m_players.length) {
					m_iOya = 0;
				}

				// 本場を増やす。
				m_honba++;

				break KYOKU_MAIN;
			}

			tsumoNokori = m_yama.getTsumoNokori();
			if (tsumoNokori == 0) {
				m_isLast = true;
			} else if (tsumoNokori < 66) {
				m_isChiihou = false;
			}
			//Log.i(TAG, "nokori = " + tsumoNokori + ", isChiihou = " + m_isChiihou);

			// イベント（ツモ）を発行する。
			retEid = tsumoEvent();

			// イベントを処理する。
			switch (retEid) {
			case TSUMO_AGARI:// ツモあがり
				if (activePlayer.isReach()) {
					m_setting.setDoraHais(m_yama.getAllDoraHais());
				}
				//getAgariScore(activePlayer.getTehai(), m_tsumoHai);
				m_score = new AgariScore();
				m_score.getAgariScore(activePlayer.getTehai(), m_tsumoHai, combis, m_setting, m_agariInfo, m_view.getResources());

				iPlayer = m_kazeToPlayerIdx[m_kazeFrom];
				if (m_iOya == iPlayer) {
					score = m_agariInfo.m_score.m_oyaRon + (m_honba * 300);
					for (int i = 0; i < 3; i++) {
						iPlayer = (iPlayer + 1) % 4;
						m_players[iPlayer].reduceTenbou(m_agariInfo.m_score.m_oyaTsumo + (m_honba * 100));
					}
				} else {
					score = m_agariInfo.m_score.m_koRon + (m_honba * 300);
					for (int i = 0; i < 3; i++) {
						iPlayer = (iPlayer + 1) % 4;
						if (m_iOya == iPlayer) {
							m_players[iPlayer].reduceTenbou(m_agariInfo.m_score.m_oyaTsumo + (m_honba * 100));
						} else {
							m_players[iPlayer].reduceTenbou(m_agariInfo.m_score.m_koTsumo + (m_honba * 100));
						}
					}
				}

				activePlayer.increaseTenbou(score);
				m_agariInfo.m_agariScore = score - (m_honba * 300);

				// 点数を清算する。
				activePlayer.increaseTenbou(m_reachbou * 1000);

				// リーチ棒の数を初期化する。
				m_reachbou = 0;

				
				// UIイベント（ツモあがり）を発行する。
				m_view.event(retEid, m_kazeFrom, m_kazeTo);

				// 親を更新する。
				if (m_iOya != m_kazeToPlayerIdx[m_kazeFrom]) {
					m_iOya++;
					if (m_iOya >= m_players.length) {
						m_iOya = 0;
					}
					m_honba = 0;
				} else {
					m_renchan = true;
					m_honba++;
				}

				break KYOKU_MAIN;
			case RON_AGARI:// ロン
				if (activePlayer.isReach()) {
					m_setting.setDoraHais(m_yama.getAllDoraHais());
				}
				m_score = new AgariScore();
				m_score.getAgariScore(activePlayer.getTehai(), m_suteHai, combis, m_setting, m_agariInfo, m_view.getResources());

				if (m_iOya == m_kazeToPlayerIdx[m_kazeFrom]) {
					score = m_agariInfo.m_score.m_oyaRon + (m_honba * 300);
				} else {
					score = m_agariInfo.m_score.m_koRon + (m_honba * 300);
				}

				m_players[m_kazeToPlayerIdx[m_kazeFrom]].increaseTenbou(score);
				m_players[m_kazeToPlayerIdx[m_kazeTo]].reduceTenbou(score);

				m_agariInfo.m_agariScore = score - (m_honba * 300);

				// 点数を清算する。
				activePlayer.increaseTenbou(m_reachbou * 1000);

				// リーチ棒の数を初期化する。
				m_reachbou = 0;
				
				// UIイベント（ロン）を発行する。
				m_view.event(retEid, m_kazeFrom, m_kazeTo);

				// 親を更新する。
				if (m_iOya != m_kazeToPlayerIdx[m_kazeFrom]) {
					m_iOya++;
					if (m_iOya >= m_players.length) {
						m_iOya = 0;
					}
					m_honba = 0;
				} else {
					m_renchan = true;
					m_honba++;
				}

				break KYOKU_MAIN;
			default:
				break;
			}

			// イベントを発行した風を更新する。
			m_kazeFrom++;
			if (m_kazeFrom >= m_players.length) {
				m_kazeFrom = 0;
			}
		}
	}

	/**
	 * プレイヤーの自風を設定する。
	 */
	private void setJikaze() {
		for (int i = 0, j = m_iOya; i < m_players.length; i++, j++) {
			if (j >= m_players.length) {
				j = 0;
			}

			// プレイヤーの自風を設定する。
			m_players[j].setJikaze(i);

			// 風をプレイヤーインデックスに変換する配列を設定する。
			m_kazeToPlayerIdx[i] = j;
		}
	}

	/**
	 * 山に割れ目を設定する。
	 *
	 * @param sais
	 *            サイコロの配列
	 */
	void setWareme(Sai[] sais) {
		int sum = sais[0].getNo() + sais[1].getNo() - 1;

		//mWareme = sum % 4;

		int startHaisIdx = ((sum % 4) * 36) + sum;

		m_yama.setTsumoHaisStartIndex(startHaisIdx);
	}

	/**
	 * 配牌する。
	 */
	private void haipai() {
		for (int i = 0, j = m_iOya, max = m_players.length * 13; i < max; i++, j++) {
			if (j >= m_players.length) {
				j = 0;
			}

			m_players[j].getTehai().addJyunTehai(m_yama.tsumo());
		}

		boolean test = false;
		if (test)
		{
			int iPlayer = 0;
			while (m_players[iPlayer].getTehai().getJyunTehaiLength() > 0) {
				m_players[iPlayer].getTehai().rmJyunTehai(0);
			}
			int haiIds[] = {27, 27, 27, 28, 28, 28, 0, 0, 1, 2, 3, 4, 5, 6};
			//int haiIds[] = {0, 1, 2, 3, 4, 5, 6, 7, 8, 33, 33, 33, 31, 31};
			//int haiIds[] = {29, 29, 29, 30, 30, 30, 31, 31, 31, 32, 32, 33, 33, 33};
			//int haiIds[] = {0, 1, 2, 3, 4, 5, 6, 7, 31, 31, 33, 33, 33};
			//int haiIds[] = {0, 1, 2, 10, 11, 12, 13, 14, 15, 31, 31, 33, 33, 33};
			//int haiIds[] = {0, 1, 2, 3, 4, 5, 6, 7, 31, 31, 32, 32, 32};
			//int haiIds[] = {0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 8, 8};
			//int haiIds[] = {1, 1, 3, 3, 5, 5, 7, 7, 30, 30, 31, 31, 32, 32};
			//int haiIds[] = {1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7};
			//int haiIds[] = {0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 8, 8};
			//int haiIds[] = {27, 27, 28, 28, 29, 29, 30, 30, 31, 31, 32, 32, 33, 33};
			//int haiIds[] = {0, 0, 0, 0, 8, 8, 8, 8, 9, 9, 9, 9, 18, 18};
			//int haiIds[] = {0, 8, 9, 17, 18, 26, 27, 28, 29, 30, 31, 32, 33, 34};
			//int haiIds[] = {0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 8, 8};
			//int haiIds[] = {1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7};
			//int haiIds[] = {19, 19, 20, 20, 21, 21, 23, 23, 23, 23, 25, 25, 25, 25};
			//int haiIds[] = {0, 0, 0, 8, 8, 8, 9, 9, 9, 17, 17, 17, 18, 18};
			//int haiIds[] = {0, 0, 0, 0, 8, 8, 8, 8, 9, 9, 9, 9, 18, 18};
			//int haiIds[] = {0, 0, 0, 8, 8, 8, 9, 9, 9, 18, 18, 18, 26, 26};
			//int haiIds[] = {27, 27, 27, 28, 28, 28, 29, 29, 29, 30, 30, 31, 31, 31};
			//int haiIds[] = {31, 31, 31, 32, 32, 32, 33, 33, 33, 30, 30, 30, 29, 29};
			//int haiIds[] = {0, 0, 1, 1, 2, 2, 6, 6, 7, 7, 8, 8, 9, 9};
			//int haiIds[] = {31, 31, 31, 32, 32, 32, 33, 33, 3, 4, 5, 6, 7, 8};
			//int haiIds[] = {1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4};
			//int haiIds[] = {0, 0, 0, 9, 9, 9, 18, 18, 18, 27, 27, 29, 28, 28};
			//int haiIds[] = {0, 0, 0, 9, 9, 9, 18, 18, 18, 27, 27, 28, 28, 28};
			//int haiIds[] = {0, 0, 0, 9, 9, 9, 18, 18, 18, 5, 6, 7, 27, 27};
			//int haiIds[] = {0, 0, 0, 2, 2, 2, 3, 3, 3, 5, 6, 7, 27, 27};
			//int haiIds[] = {0, 0, 0, 2, 2, 2, 3, 3, 3, 4, 4, 4, 10, 10};
			//int haiIds[] = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 9, 9, 10, 10}; // イッツー
			//int haiIds[] = {0, 1, 2, 9, 10, 11, 18, 19, 20, 33, 33, 33, 27, 27};
			//int haiIds[] = {1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4}; // リーチタンピンイーペーコー
			//int haiIds[] = {1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7}; // リーチタンピンイーペーコー
			//int haiIds[] = {1, 1, 2, 2, 3, 3, 4, 5, 6, 10, 10, 10, 11, 12}; // リーチタンピンイーペーコー
			for (int i = 0; i < haiIds.length - 1; i++) {
				m_players[iPlayer].getTehai().addJyunTehai(new Hai(haiIds[i]));
			}
			//m_players[iPlayer].getTehai().rmJyunTehai(0);
			//m_players[iPlayer].getTehai().setPon(new Hai(0), getRelation(this.m_kazeFrom, this.m_kazeTo));
			//m_players[iPlayer].getTehai().setPon(new Hai(31), getRelation(this.m_kazeFrom, this.m_kazeTo));
			//m_players[iPlayer].getTehai().rmJyunTehai(0);
			//m_players[iPlayer].getTehai().setChiiLeft(new Hai(0), getRelation(this.m_kazeFrom, this.m_kazeTo));
			//m_players[iPlayer].getKawa().add(new Hai(0));
		}
	}

	boolean m_isTenhou = false;
	boolean m_isChiihou = false;
	boolean m_isTsumo = false;
	boolean m_isRinshan = false;
	boolean m_isLast = false;

	/**
	 * イベント（ツモ）を発行する。
	 *
	 * @return イベントID
	 */
	private EventId tsumoEvent() {
		// アクティブプレイヤーを設定する。
		activePlayer = m_players[m_kazeToPlayerIdx[m_kazeFrom]];

		m_isTsumo = true;

		//m_tsumoHai = new Hai(13, true);
		// UIイベント（ツモ）を発行する。
		
		m_view.event(EventId.TSUMO, m_kazeFrom, m_kazeFrom);

		// イベント（ツモ）を発行する。
		EventId retEid = activePlayer.getEventIf().event(EventId.TSUMO, m_kazeFrom, m_kazeFrom);
		Log.i(TAG, retEid.toString() + ", kazeFrom = " + m_kazeFrom + ", kazeTo = " + m_kazeTo);

		m_isTenhou = false;

		m_isTsumo = false;
		
		// UIイベント（進行待ち）を発行する。
		m_view.event(EventId.UI_WAIT_PROGRESS, m_kazeFrom, m_kazeFrom);

		int sutehaiIdx;
		Hai[] kanHais;

		if (retEid != EventId.REACH) {
			activePlayer.setIppatsu(false);
		}

		// イベントを処理する。
		switch (retEid) {
		case ANKAN:
			m_isChiihou = false;

			activePlayer.getTehai().addJyunTehai(m_tsumoHai);
			sutehaiIdx = activePlayer.getEventIf().getISutehai();
			kanHais = m_playerAction.getKanHais();
			activePlayer.getTehai().setAnKan(kanHais[sutehaiIdx], getRelation(this.m_kazeFrom, this.m_kazeTo));

			// イベントを通知する。
			retEid = notifyEvent(EventId.ANKAN, m_kazeFrom, m_kazeFrom);
			
			// UIイベント（進行待ち）を発行する。
			m_view.event(EventId.UI_WAIT_PROGRESS, KAZE_NONE, KAZE_NONE);

			// ツモ牌を取得する。
			m_tsumoHai = m_yama.rinshanTsumo();

			// イベント（ツモ）を発行する。
			m_isRinshan = true;
			retEid = tsumoEvent();
			m_isRinshan = false;
			break;
		case TSUMO_AGARI:// ツモあがり
			break;
		case SUTEHAI:// 捨牌
			// 捨牌のインデックスを取得する。
			sutehaiIdx = activePlayer.getEventIf().getISutehai();

			// 理牌の間をとる。
			m_infoUi.setSutehaiIdx(sutehaiIdx);
			
			m_view.event(EventId.UI_WAIT_RIHAI, m_kazeFrom, m_kazeFrom);

			if (sutehaiIdx >= activePlayer.getTehai().getJyunTehaiLength()) {// ツモ切り
				Hai.copy(m_suteHai, m_tsumoHai);
				activePlayer.getKawa().add(m_suteHai);
			} else {// 手出し
				activePlayer.getTehai().copyJyunTehaiIndex(m_suteHai, sutehaiIdx);
				activePlayer.getTehai().rmJyunTehai(sutehaiIdx);
				activePlayer.getTehai().addJyunTehai(m_tsumoHai);
				activePlayer.getKawa().add(m_suteHai);
				activePlayer.getKawa().setTedashi(true);
			}
			m_suteHais[m_suteHaisCount++] = new SuteHai(m_suteHai);

			if (!activePlayer.isReach()) {
				activePlayer.setSuteHaisCount(m_suteHaisCount);
			}

			// イベントを通知する。
			retEid = notifyEvent(EventId.SUTEHAI, m_kazeFrom, m_kazeFrom);
			break;
		case REACH:
			// 捨牌のインデックスを取得する。
			sutehaiIdx = activePlayer.getEventIf().getISutehai();
			activePlayer.setReach(true);
			if (m_isChiihou) {
				activePlayer.setDoubleReach(true);
			}
			activePlayer.setSuteHaisCount(m_suteHaisCount);
			
			m_view.event(EventId.UI_WAIT_RIHAI, m_kazeFrom, m_kazeFrom);

			if (sutehaiIdx >= activePlayer.getTehai().getJyunTehaiLength()) {// ツモ切り
				Hai.copy(m_suteHai, m_tsumoHai);
				activePlayer.getKawa().add(m_suteHai);
				activePlayer.getKawa().setReach(true);
			} else {// 手出し
				activePlayer.getTehai().copyJyunTehaiIndex(m_suteHai, sutehaiIdx);
				activePlayer.getTehai().rmJyunTehai(sutehaiIdx);
				activePlayer.getTehai().addJyunTehai(m_tsumoHai);
				activePlayer.getKawa().add(m_suteHai);
				activePlayer.getKawa().setTedashi(true);
				activePlayer.getKawa().setReach(true);
			}
			m_suteHais[m_suteHaisCount++] = new SuteHai(m_suteHai);

			activePlayer.reduceTenbou(1000);
			activePlayer.setReach(true);
			m_reachbou++;

			activePlayer.setIppatsu(true);

			// イベントを通知する。
			retEid = notifyEvent(EventId.REACH, m_kazeFrom, m_kazeFrom);
			break;
		default:
			break;
		}

		return retEid;
	}

	/**
	 * イベントを通知する。
	 *
	 * @param a_eventId
	 *            イベントID
	 * @param a_kazeFrom
	 *            イベントを発行した風
	 * @param a_kazeTo
	 *            イベントの対象となった風
	 * @return イベントID
	 */
	private EventId notifyEvent(EventId a_eventId, int a_kazeFrom, int a_kazeTo) {
		// UIイベントを発行する。
		
		m_view.event(a_eventId, a_kazeFrom, a_kazeTo);

		EventId ret = EventId.NAGASHI;
		int iSuteHai;

		switch (a_eventId) {
		case PON:
		case CHII_CENTER:
		case CHII_LEFT:
		case CHII_RIGHT:
		case DAIMINKAN:
		case SUTEHAI:
		case REACH:
			for (int i = 0, j = a_kazeFrom + 1; i < m_players.length - 1; i++, j++) {
				if (j >= m_players.length) {
					j = 0;
				}

				// アクティブプレイヤーを設定する。
				activePlayer = m_players[m_kazeToPlayerIdx[j]];

				ret = activePlayer.getEventIf().event(EventId.RON_CHECK, a_kazeFrom, j);
				if (ret == EventId.RON_AGARI) {
					// アクティブプレイヤーを設定する。
					this.m_kazeFrom = j;
					this.m_kazeTo = a_kazeFrom;
					activePlayer = m_players[m_kazeToPlayerIdx[this.m_kazeFrom]];
					return ret;
				}
			}
			break;
		}

		// 各プレイヤーにイベントを通知する。
		NOTIFYLOOP: for (int i = 0, j = a_kazeFrom; i < m_players.length; i++, j++) {
			if (j >= m_players.length) {
				j = 0;
			}

			// アクティブプレイヤーを設定する。
			activePlayer = m_players[m_kazeToPlayerIdx[j]];

			// イベントを発行する。
			a_kazeTo = j;
			ret = activePlayer.getEventIf().event(a_eventId, a_kazeFrom, a_kazeTo);

			if (ret != EventId.NAGASHI) {
				for (int k = 0; k < 4; k++) {
					m_players[k].setIppatsu(false);
				}
			}

			// イベントを処理する。
			switch (ret) {
			case TSUMO_AGARI:// ツモあがり
				// アクティブプレイヤーを設定する。
				this.m_kazeFrom = j;
				this.m_kazeTo = a_kazeTo;
				activePlayer = m_players[m_kazeToPlayerIdx[this.m_kazeFrom]];
				break NOTIFYLOOP;
			case RON_AGARI:// ロン
				// アクティブプレイヤーを設定する。
				this.m_kazeFrom = a_kazeTo;
				this.m_kazeTo = a_kazeFrom;
//				this.m_kazeFrom = j;
//				this.m_kazeTo = toKaze;
				activePlayer = m_players[m_kazeToPlayerIdx[this.m_kazeFrom]];
				break NOTIFYLOOP;
			case PON:
				m_isChiihou = false;
				// アクティブプレイヤーを設定する。
				this.m_kazeFrom = j;
				this.m_kazeTo = a_kazeFrom;
				activePlayer = m_players[m_kazeToPlayerIdx[this.m_kazeFrom]];
				activePlayer.getTehai().setPon(m_suteHai, getRelation(this.m_kazeFrom, this.m_kazeTo));
				m_players[m_kazeToPlayerIdx[this.m_kazeTo]].getKawa().setNaki(true);

				notifyEvent(EventId.SELECT_SUTEHAI, this.m_kazeFrom, this.m_kazeTo);

				// 捨牌のインデックスを取得する。
				iSuteHai = activePlayer.getEventIf().getISutehai();
				activePlayer.getTehai().copyJyunTehaiIndex(m_suteHai, iSuteHai);
				activePlayer.getTehai().rmJyunTehai(iSuteHai);
				activePlayer.getKawa().add(m_suteHai);
				//activePlayer.getKawa().setNaki(true);
				activePlayer.getKawa().setTedashi(true);
				m_suteHais[m_suteHaisCount++] = new SuteHai(m_suteHai);

				// イベントを通知する。
				ret = notifyEvent(EventId.PON, this.m_kazeFrom, this.m_kazeTo);
				break NOTIFYLOOP;
			case CHII_LEFT:
				m_isChiihou = false;
				// アクティブプレイヤーを設定する。
				this.m_kazeFrom = j;
				this.m_kazeTo = a_kazeFrom;
				activePlayer = m_players[m_kazeToPlayerIdx[this.m_kazeFrom]];
				activePlayer.getTehai().setChiiLeft(m_suteHai, getRelation(this.m_kazeFrom, this.m_kazeTo));
				m_players[m_kazeToPlayerIdx[this.m_kazeTo]].getKawa().setNaki(true);

				notifyEvent(EventId.SELECT_SUTEHAI, this.m_kazeFrom, this.m_kazeTo);

				// 捨牌のインデックスを取得する。
				iSuteHai = activePlayer.getEventIf().getISutehai();
				activePlayer.getTehai().copyJyunTehaiIndex(m_suteHai, iSuteHai);
				activePlayer.getTehai().rmJyunTehai(iSuteHai);
				activePlayer.getKawa().add(m_suteHai);
				//activePlayer.getKawa().setNaki(true);
				activePlayer.getKawa().setTedashi(true);
				m_suteHais[m_suteHaisCount++] = new SuteHai(m_suteHai);

				// イベントを通知する。
				ret = notifyEvent(EventId.CHII_LEFT, this.m_kazeFrom, this.m_kazeTo);
				break NOTIFYLOOP;
			case CHII_CENTER:
				m_isChiihou = false;
				// アクティブプレイヤーを設定する。
				this.m_kazeFrom = j;
				this.m_kazeTo = a_kazeFrom;
				activePlayer = m_players[m_kazeToPlayerIdx[this.m_kazeFrom]];
				activePlayer.getTehai().setChiiCenter(m_suteHai, getRelation(this.m_kazeFrom, this.m_kazeTo));
				m_players[m_kazeToPlayerIdx[this.m_kazeTo]].getKawa().setNaki(true);

				notifyEvent(EventId.SELECT_SUTEHAI, this.m_kazeFrom, this.m_kazeTo);

				// 捨牌のインデックスを取得する。
				iSuteHai = activePlayer.getEventIf().getISutehai();
				activePlayer.getTehai().copyJyunTehaiIndex(m_suteHai, iSuteHai);
				activePlayer.getTehai().rmJyunTehai(iSuteHai);
				activePlayer.getKawa().add(m_suteHai);
				//activePlayer.getKawa().setNaki(true);
				activePlayer.getKawa().setTedashi(true);
				m_suteHais[m_suteHaisCount++] = new SuteHai(m_suteHai);

				// イベントを通知する。
				ret = notifyEvent(EventId.CHII_CENTER, this.m_kazeFrom, this.m_kazeTo);
				break NOTIFYLOOP;
			case CHII_RIGHT:
				m_isChiihou = false;
				// アクティブプレイヤーを設定する。
				this.m_kazeFrom = j;
				this.m_kazeTo = a_kazeFrom;
				activePlayer = m_players[m_kazeToPlayerIdx[this.m_kazeFrom]];
				activePlayer.getTehai().setChiiRight(m_suteHai, getRelation(this.m_kazeFrom, this.m_kazeTo));
				m_players[m_kazeToPlayerIdx[this.m_kazeTo]].getKawa().setNaki(true);

				notifyEvent(EventId.SELECT_SUTEHAI, this.m_kazeFrom, this.m_kazeTo);

				// 捨牌のインデックスを取得する。
				iSuteHai = activePlayer.getEventIf().getISutehai();
				activePlayer.getTehai().copyJyunTehaiIndex(m_suteHai, iSuteHai);
				activePlayer.getTehai().rmJyunTehai(iSuteHai);
				activePlayer.getKawa().add(m_suteHai);
				//activePlayer.getKawa().setNaki(true);
				activePlayer.getKawa().setTedashi(true);
				m_suteHais[m_suteHaisCount++] = new SuteHai(m_suteHai);

				// イベントを通知する。
				ret = notifyEvent(EventId.CHII_RIGHT, this.m_kazeFrom, this.m_kazeTo);
				break NOTIFYLOOP;
			case DAIMINKAN:
				m_isChiihou = false;
				// アクティブプレイヤーを設定する。
				this.m_kazeFrom = j;
				this.m_kazeTo = a_kazeFrom;
				activePlayer = m_players[m_kazeToPlayerIdx[this.m_kazeFrom]];
				activePlayer.getTehai().setDaiMinKan(m_suteHai, getRelation(this.m_kazeFrom, this.m_kazeTo));
				m_players[m_kazeToPlayerIdx[this.m_kazeTo]].getKawa().setNaki(true);

				// イベントを通知する。
				ret = notifyEvent(EventId.DAIMINKAN, this.m_kazeFrom, this.m_kazeTo);

				// UIイベント（進行待ち）を発行する。
				
				m_view.event(EventId.UI_WAIT_PROGRESS, KAZE_NONE, KAZE_NONE);

				// ツモ牌を取得する。
				m_tsumoHai = m_yama.rinshanTsumo();

				// イベント（ツモ）を発行する。
				m_isRinshan = true;
				ret = tsumoEvent();
				m_isRinshan = false;
				break NOTIFYLOOP;
			default:
				break;
			}

			if (a_eventId == EventId.SELECT_SUTEHAI) {
				return ret;
			}
		}

		// アクティブプレイヤーを設定する。
		activePlayer = m_players[m_kazeToPlayerIdx[a_kazeFrom]];

		return ret;
	}

	/*
	 * Info, InfoUIに提供するAPIを定義する。
	 */

	/**
	 * 表ドラ、槓ドラの配列を取得する。
	 *
	 * @return 表ドラ、槓ドラの配列
	 */
	Hai[] getDoras() {
		return getYama().getOmoteDoraHais();
	}

	/**
	 * 表ドラ、槓ドラの配列を取得する。
	 *
	 * @return 表ドラ、槓ドラの配列
	 */
	Hai[] getUraDoras() {
		return getYama().getUraDoraHais();
	}

	/**
	 * 自風を取得する。
	 */
	int getJikaze() {
		return activePlayer.getJikaze();
	}

	/**
	 * 本場を取得する。
	 *
	 * @return 本場
	 */
	int getHonba() {
		return m_honba;
	}

	/**
	 * リーチを取得する。
	 *
	 * @param kaze
	 *            風
	 * @return リーチ
	 */
	boolean isReach(int kaze) {
		return m_players[m_kazeToPlayerIdx[kaze]].isReach();
	}

	/**
	 * 手牌をコピーする。
	 *
	 * @param tehai
	 *            手牌
	 * @param kaze
	 *            風
	 */
	void copyTehai(Tehai tehai, int kaze) {
		if (activePlayer.getJikaze() == kaze) {
			Tehai.copy(tehai, activePlayer.getTehai(), true);
		} else {
			Tehai.copy(tehai, m_players[m_kazeToPlayerIdx[kaze]].getTehai(), false);
		}
	}

	/**
	 * 手牌をコピーする。
	 *
	 * @param tehai
	 *            手牌
	 * @param kaze
	 *            風
	 */
	void copyTehaiUi(Tehai tehai, int kaze) {
		Tehai.copy(tehai, m_players[m_kazeToPlayerIdx[kaze]].getTehai(), true);
	}

	/**
	 * 河をコピーする。
	 *
	 * @param kawa
	 *            河
	 * @param kaze
	 *            風
	 */
	void copyKawa(Hou kawa, int kaze) {
		Hou.copy(kawa, m_players[m_kazeToPlayerIdx[kaze]].getKawa());
	}

	/**
	 * ツモの残り数を取得する。
	 *
	 * @return ツモの残り数
	 */
	int getTsumoRemain() {
		return m_yama.getTsumoNokori();
	}

	String getName(int kaze) {
		return m_players[m_kazeToPlayerIdx[kaze]].getEventIf().getName();
	}

	int getTenbou(int kaze) {
		return m_players[m_kazeToPlayerIdx[kaze]].getTenbou();
	}

	private Combi[] combis = new Combi[10];
	{
		for (int i = 0; i < combis.length; i++)
			combis[i] = new Combi();
	}

	AgariScore m_score;
	AgariInfo m_agariInfo = new AgariInfo();

	public AgariInfo getAgariInfo() {
		return m_agariInfo;
	}

	public int getBakaze() {
		if (m_kyoku <= KYOKU_TON_4) {
			return KAZE_TON;
		} else {
			return KAZE_NAN;
		}
	}

	AgariSetting m_setting;
	public int getAgariScore(Tehai tehai, Hai addHai) {
		AgariSetting setting = new AgariSetting(this);
		m_setting = setting;
		setting.setDoraHais(getDoras());
		if (activePlayer.isReach()) {
			if (activePlayer.isDoubleReach()) {
				setting.setYakuflg(YakuflgName.DOUBLEREACH.ordinal(), true);
			} else {
				setting.setYakuflg(YakuflgName.REACH.ordinal(), true);
			}
		}
		if (m_isTsumo) {
			setting.setYakuflg(YakuflgName.TUMO.ordinal(), true);
			if (m_isTenhou) {
				setting.setYakuflg(YakuflgName.TENHOU.ordinal(), true);
			} else if (m_isChiihou) {
				setting.setYakuflg(YakuflgName.TIHOU.ordinal(), true);
			}
		}
		if (m_isTsumo && m_isRinshan) {
			setting.setYakuflg(YakuflgName.RINSYAN.ordinal(), true);
		}
		if (m_isLast) {
			if (m_isTsumo) {
				setting.setYakuflg(YakuflgName.HAITEI.ordinal(), true);
			} else {
				setting.setYakuflg(YakuflgName.HOUTEI.ordinal(), true);
			}
		}
		if (activePlayer.isIppatsu()) {
			setting.setYakuflg(YakuflgName.IPPATU.ordinal(), true);
		}
		if (m_view.isKuitan()) {
			setting.setYakuflg(YakuflgName.KUITAN.ordinal(), true);
		}
		m_score = new AgariScore();
		int score = m_score.getAgariScore(tehai, addHai, combis, setting, m_agariInfo, m_view.getResources());
		return score;
	}

//	public String[] getYakuName(Tehai tehai, Hai addHai){
//		AgariSetting setting = new AgariSetting(this);
//		AgariScore score = new AgariScore();
//		return score.getYakuName(tehai, addHai, combis, setting, m_view.getResources());
//	}

	@Override
	public void run() {
		// ゲームを開始する。
		play();
	}

	public void setSutehaiIdx(int sutehaiIdx) {
		m_info.setSutehaiIdx(sutehaiIdx);
	}

	public void postUiEvent(EventId a_eventId, int a_kazeFrom, int a_kazeTo) {
		
		m_view.event(a_eventId, a_kazeFrom, a_kazeTo);
	}
}
