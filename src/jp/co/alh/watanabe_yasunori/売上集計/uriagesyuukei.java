package jp.co.alh.watanabe_yasunori.売上集計;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class uriagesyuukei {

	public static void main(String[] args) {
		try {
			// 店舗定義用MAPと商品定義用MAP
			HashMap<String, String> branchmap = new HashMap<String, String>();
			HashMap<String, String> commoditymap = new HashMap<String, String>();
			HashMap<String, Long> branchsalesmap = new HashMap<String, Long>();

			HashMap<String, Long> commoditysalesmap = new HashMap<String, Long>();
			String berror = "支店定義ファイルのフォーマットが不正です";
			String eerror = "商品定義ファイルのフォーマットが不正です";

			BufferedReader branchbr = null;
			// branchデータファイルを読み込み
			try {
				// File branchfile = new File(args[0], "branch.lst");
				// エラーテスト用
				File branchfile = new File(args[0], "errortest.lst");

				FileReader branchfr = new FileReader(branchfile);

				branchbr = new BufferedReader(branchfr);

				// エラー処理

				// データファイルを文字列に
				String branch_s;

				// 繰り返してデータファイルを一行ずつ読み込む
				while ((branch_s = branchbr.readLine()) != null) {

					// System.out.println(s);
					String branchstr = branch_s;
					String[] branch_sp = branchstr.split(",");
					// 読み込んだデータをそれぞれのMAPに覚えさせる
					branchmap.put(branch_sp[0], branch_sp[1]);

					// 読み込んだ支店番号を数字かどうか判断する(作成中)

					// 支店番号が数字で３桁でなければエラーを返す
					if (branch_sp[0].matches("[0-9]{3}")) {
						System.out.println(branch_sp[0] + "支店定義ファイルは数字です");

					} else {
						System.out.println(berror);
						System.exit(0);
					}

				}

			} catch (IOException e) {
				System.out.println(berror);
				System.exit(0);

			} finally {
				branchbr.close();

			}
			// 上記の処理を商品定義にて
			try {
				File commodityfile = new File(args[0], "commodity.lst");
				FileReader commodityfr = new FileReader(commodityfile);
				BufferedReader commoditybr = new BufferedReader(commodityfr);

				String commodity_s;

				while ((commodity_s = commoditybr.readLine()) != null) {

					String commoditystr = commodity_s;
					String[] commodity_sp = commoditystr.split(",");
					// 読み込んだデータをそれぞれのMAPに覚えさせる
					commoditymap.put(commodity_sp[0], commodity_sp[1]);
					// System.out.println(commodity_sp[0]);
					if (commodity_sp[0].matches("[A-Za-z0-9]{8}")) {

					} else {
						System.out.println(eerror);
					}
				}

				commoditybr.close();

			} catch (IOException e) {
				System.out.println(eerror);

			}

			// レジストリ内のファイルデータ読み込み
			ArrayList<File> extraction = new ArrayList<File>();



				try {
					File files1 = new File(args[0]);
					// File extraction = new File(args[0]);
					File flist[] = files1.listFiles();

					// String extractionList[] = extraction.list();

					//String fl = "0";

					for (int i = 0; i < flist.length; i++) {
						// System.out.println(flist[i]);
						// 読み込んだレジストリから『数字八桁』かつ『.rcd』のファイルを選別しリストに当て込む
						if (flist[i].getName().matches("\\d{8}.rcd$") && flist[i].isFile()) {
							extraction.add(flist[i]);
						}else{

						}
					}

					for(int n =0; n<extraction.size(); n++){
							String fliststr =extraction.get(n).getName();
							String[] flistSp = fliststr.split("\\.");
							int fsp = Integer.parseInt(flistSp[0]);

							if (fsp == n + 1) {

								System.out.println(fsp+"正常");
							} else {
								System.out.println("売上ファイル名が連番ではありません");
								//System.out.println(n);
								//System.out.println(fsp);
								return;
							}
						}

				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("ファイルがありません");

				}


			// System.out.println(extraction.get(2));

			String sales_s;
			BufferedWriter bw = null;
			// int i =0;
			for (int i = 0; i < extraction.size(); i++) {

				ArrayList<String> Allocation = new ArrayList<String>();
				// 抽出した売り上げデータからファイルを読み込む
				File salesfile = extraction.get(i);

				try {

					FileReader salesfr = new FileReader(salesfile);
					BufferedReader salesbr = new BufferedReader(salesfr);

					// System.out.print(salesbr);
					// System.out.println(salesbr);

					// ファイル内の支店番号と売り上げ金額、商品コードをリストへ当て込み
					while ((sales_s = salesbr.readLine()) != null) {
						// ファイルからリストへ
						Allocation.add(sales_s);

					}
					// 支店別売上mapの現在の支店の売上値がnullだったらそのまま挿入。nullでなければその数値に＋する
					if (branchsalesmap.get(Allocation.get(0)) == null) {

						// リストから売上額をStringからlongへ
						long branchAllocationLg = Long.parseLong(Allocation.get(2));
						// リストからマップへ
						branchsalesmap.put(Allocation.get(0), branchAllocationLg);
					} else {
						long x = branchsalesmap.get(Allocation.get(0));
						// リストから売上額をStringからlongへ
						long branchAllocationLg = Long.parseLong(Allocation.get(2));
						long branchsum = x + branchAllocationLg;
						// リストからマップへ
						branchsalesmap.put(Allocation.get(0), branchsum);
					}

					// 商品別売上mapの現在の商品売上値がnullだったらそのまま挿入。nullでなければその数値に+する。
					if (commoditysalesmap.get(Allocation.get(1)) == null) {

						// リストから売上額をStringからlongへ
						long commodityAllocationLg = Long.parseLong(Allocation.get(2));
						// リストからマップへ
						commoditysalesmap.put(Allocation.get(1), commodityAllocationLg);
					} else {
						// long y = commoditysalesmap.get(Allocation.get(1));
						// リストから売上額をStringからlongへ
						long commodityAllocationLg = Long.parseLong(Allocation.get(2));
						long commoditysum = commodityAllocationLg;
						// リストからマップへ
						commoditysalesmap.put(Allocation.get(1), commoditysum);

						// System.out.println("==================");
						// System.out.println(branchAllocation);
						salesbr.close();

					}
					// System.out.println("テスト"+Allocation);
				}

				catch (Exception e) {
					System.out.println(e);
				} finally {

				}
			}

			// 店舗別売上金集計

			// mapをリストに入れて並び替えコンバートする
			List<Map.Entry<String, Long>> branchsalesEntry = new ArrayList<Map.Entry<String, Long>>(
					branchsalesmap.entrySet());
			Collections.sort(branchsalesEntry, new Comparator<Map.Entry<String, Long>>() {

				public int compare(Entry<String, Long> entry1, Entry<String, Long> entry2) {
					return ((Long) entry2.getValue()).compareTo((Long) entry1.getValue());

				}

			});
			try {
				// 支店別集計ファイルの出力作成
				// ファイルを変換
				File branchTotal = new File(args[0], "branch.out");
				FileWriter fw = new FileWriter(branchTotal);
				bw = new BufferedWriter(fw);
				for (Entry<String, Long> be : branchsalesEntry) {

					// 書き出す内容

					// System.out.println();
					bw.write(be.getKey() + "," + branchmap.get(be.getKey()) + "," + be.getValue() + "\r\n");

				}

			} catch (IOException e) {
				System.out.println(e);

			} finally {
				bw.close();

			}

			try {
				// mapをリストに入れて並び替えコンバートする
				List<Map.Entry<String, Long>> commoditysalesEntry = new ArrayList<Map.Entry<String, Long>>(
						commoditysalesmap.entrySet());
				Collections.sort(commoditysalesEntry, new Comparator<Map.Entry<String, Long>>() {

					public int compare(Entry<String, Long> entry1, Entry<String, Long> entry2) {
						return ((Long) entry2.getValue()).compareTo((Long) entry1.getValue());

					}

				});
				// 商品別集計ファイルの出力作成
				// ファイルを変換
				File commodityTotal = new File(args[0], "commodity.out");
				FileWriter fw = new FileWriter(commodityTotal);
				bw = new BufferedWriter(fw);
				for (Entry<String, Long> ce : commoditysalesEntry) {

					// 書き出す内容
					// Allocation.put=(
					// s.getKey() + "," + branchmap.get(s.getKey()) + "," +
					// branchsalesmap.get(s.getKey()));
					// System.out.println(AllocationList.get(0)+","+branchmap.get(1)+branchsalesmap.get(1));
					bw.write(ce.getKey() + "," + commoditymap.get(ce.getKey()) + "," + ce.getValue() + "\r\n");

				}

			} catch (IOException e) {
				System.out.println(e);

			} finally {
				bw.close();

			}

		} catch (Exception e) {

		}

		// System.out.println(s.getKey());
		// System.out.println("s.getValue():"+s.getValue());

	}
	// System.out.println(.getKey());
	// System.out.println(Allocation.get(0)+","+branchmap.get(Allocation.get(0))+","+branchsalesmap.get(Allocation.get(0)));
	// System.out.println(branchsalesmap.get("001")+branchsalesmap.get("001"));

	/*
	 * System.out.println(branchsalesmap.get("001"));
	 * System.out.println(branchsalesmap.get("002"));
	 * System.out.println(branchsalesmap.get("003"));
	 * System.out.println(branchsalesmap.get("004"));
	 * System.out.println(commoditysalesmap.get("SFT00001"));
	 * System.out.println(commoditysalesmap.get("SFT00002"));
	 * System.out.println(commoditysalesmap.get("SFT00003"));
	 * System.out.println(commoditysalesmap.get("SFT00004"));
	 *
	 * //MAPに入っているデータを参照する /*
	 * System.out.println("支店番号001番は"+branchmap.get("001"));
	 * System.out.println("支店番号002番は"+branchmap.get("002"));
	 * System.out.println("支店番号003番は"+branchmap.get("003"));
	 * System.out.println("支店番号004番は"+branchmap.get("004"));
	 * System.out.println("支店番号005番は"+branchmap.get("005"));
	 * System.out.println("商品番号SFT00001は"+commoditymap.get("SFT00001"));
	 * System.out.println("商品番号SFT00002は"+commoditymap.get("SFT00002"));
	 * System.out.println(branchmap.entrySet());
	 */

}
