package jp.alhinc.watanabe_yasunori.calculate_sales;

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

public class CalculateSales {

	public static void main(String[] args) {

		ArrayList<File> extraction = new ArrayList<File>();
		HashMap<String, String> branchMap = new HashMap<String, String>();
		HashMap<String, String> commodityMap = new HashMap<String, String>();
		HashMap<String, Long> branchSalesMap = new HashMap<String, Long>();
		HashMap<String, Long> commoditySalesMap = new HashMap<String, Long>();

		if ( args.length != 1 ) {
			System.out.println( "予期せぬエラーが発生しました" );
			return;
		}
		if ( (readFile(args[0], "branch.lst", branchMap, branchSalesMap, "\\d+{3}", "支店") ) !=true ) {
			return;
		}
		if ( (readFile(args[0], "commodity.lst", commodityMap, commoditySalesMap, "[A-Z0-9]{8}", "商品") )!=true ) {
			return;
		}
		try {
			File files1 = new File(args[0]);

			if ( !files1.exists() ){
				return;
			}
			File[] fList = files1.listFiles();
			for ( int i = 0; i < fList.length; i++ ) {
				// 読み込んだレジストリから『数字八桁』かつ『.rsd』のファイルを選別しリストに当て込む
				if (( fList[i].isFile() ) && fList[i].getName().matches("\\d{8}.rcd$") ){
					extraction.add(fList[i]);
				}
			}
		} catch (Exception e) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
		// 売上ファイルが連番でなけばエラーを表示する
		for ( int i = 0; i < extraction.size(); i++ ) {
			String[] exS = extraction.get(i).getName().split("[.]");
			int exIn = Integer.parseInt( exS[0] );
			if ( (exIn -= 1) != i ) {
				System.out.println("売上ファイル名が連番になっていません");
				return;
			}
		}
		for ( int i = 0; i < extraction.size(); i++ ) {
			ArrayList<String> allocation = new ArrayList<String>();

			// 抽出した売り上げデータからファイルを読み込む
			File salesFile = extraction.get(i);
			BufferedReader br = null;
			try {
				String salesS;
				FileReader salesFr = new FileReader(salesFile);
				br = new BufferedReader(salesFr);
				// ファイル内の支店番号と売り上げ金額、商品コードをリストへ当て込み
				while ( (salesS = br.readLine() ) != null ) {
					// ファイルからリストへ
					allocation.add(salesS);
				}
				if ( allocation.size() != 3 ) {
					System.out.println(extraction.get(i).getName() + "のフォーマットが不正です");
					return;
				}
				//System.out.println("all"+allocation);
				//売上ファイルの支店番号が定義ファイルに存在しなかったらエラーを出す
				if ( !branchMap.containsKey( allocation.get(0) ) ){
					System.out.println( extraction.get(i).getName() + "の支店コードが不正です");
					return;
				}
				//売上ファイルの商品番号が定義ファイルに存在しなかったらエラーを出す
				if( !commodityMap.containsKey( allocation.get(1) ) ){
					System.out.println( extraction.get(i).getName() + "の商品コードが不正です");
					return;
				}
				if( !allocation.get(2).matches("\\d+") ){
					System.out.println( extraction.get(i).getName() + "のフォーマットが不正です");
					return;
				}
				// 支店別売上mapの現在の支店の売上数値に＋する
				long x = branchSalesMap.get( allocation.get(0) );

				// リストから売上額をStringからlongへ
				long branchAllocationLg = Long.parseLong( allocation.get(2) );
				long branchSum = x + branchAllocationLg;
				// リストからマップへ

				branchSalesMap.put( allocation.get(0),branchSum );

				// 商品別売上mapの現在の商品売上数値に+する。
				// リストから売上額をStringからlongへ
				long commodityAllocationLg = Long.parseLong( allocation.get(2) );
				long commoditySum = commodityAllocationLg;
				// リストからマップへ
				commoditySalesMap.put( allocation.get(1), commoditySum );

			} catch ( IOException e ) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			} finally {
				try {
					if( br != null ) {
						br.close();
					}
				} catch( IOException e ) {
					System.out.println("予期せぬエラーが発生しました");
					return;
				}
			}
		}
		for ( String key : branchSalesMap.keySet() ) {
			// System.out.println("val : "+branchSalesMap.get(key));
			if ( branchSalesMap.get(key) > 9999999999L || branchSalesMap.get(key) < -9999999999L ) {
				System.out.println("合計金額が10桁を超えました");
				return;
			}
		}
		for ( String key : commoditySalesMap.keySet() ) {
			// System.out.println("val : "+commoditySalesMap.get(key));
			if ( commoditySalesMap.get(key) > 9999999999L || commoditySalesMap.get(key) < -9999999999L ) {
				System.out.println("合計金額が10桁を超えました");
				return;
			}
		}
		// メソッドへファイルの書き出しをさせる
		if ( !writeFile( args[0], "branch.out", branchMap, branchSalesMap ) ) {
			// falseが返ってきたら実行を取り消しする
			return;
		}
		if ( !writeFile( args[0], "commodity.out", commodityMap, commoditySalesMap ) ) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
	}
	public static boolean writeFile(String dirpath, String filename, HashMap<String, String> namemap,
			HashMap<String, Long> salesmap) {
		List<Map.Entry<String, Long>> branchSalesEntry = new ArrayList<Map.Entry<String, Long>>(salesmap.entrySet());
		Collections.sort(branchSalesEntry, new Comparator<Map.Entry<String, Long>>() {
			public int compare(Entry<String, Long> entry1, Entry<String, Long> entry2) {
				return ((Long) entry2.getValue()).compareTo((Long) entry1.getValue());
			}
		});
		String crlf = System.getProperty("line.separator");
		BufferedWriter bw = null;
		try {
			// 支店別集計ファイルの出力作成
			// ファイルを変換
			File branchTotal = new File(dirpath, filename);
			FileWriter fw = new FileWriter(branchTotal);
			bw = new BufferedWriter(fw);

			for ( Entry<String, Long> be : branchSalesEntry ) {
				// 書き出す内容
				bw.write(be.getKey() + "," + namemap.get(be.getKey() ) + "," + be.getValue() + crlf );
			}
		} catch ( IOException e ) {
			System.out.println("予期せぬエラーが発生しました");
			return false;
		} finally {
			try {
				if ( bw != null ) {
					bw.close();
				}
			}catch ( IOException e ) {
				// TODO 自動生成された catch ブロック
				System.out.println("予期せぬエラーが発生しました");
			}
		}
		//System.out.println("OK");
		return true;
	}
	public static boolean readFile(String dirPath, String fileName, HashMap<String, String> nameMap,
			HashMap<String, Long> salesMap, String matchesCondition, String name) {

		// 店舗定義用MAPと商品定義用MAP


		// branchデータファイルを読み込み
		BufferedReader br = null;




		File nameFile = new File( dirPath, fileName );
		if ( !nameFile.exists() ){
			System.out.println( name + "定義ファイルが存在しません");
			return false;
		}
		try {
			FileReader nameFr = new FileReader(nameFile);
			br = new BufferedReader(nameFr);
			// エラー処理
			// データファイルを文字列に
			String nameS;
			// 繰り返してデータファイルを一行ずつ読み込む
			while ( ( nameS = br.readLine() ) != null ) {
				// System.out.println(branch_s);
				String nameStr = nameS;
				String[] nameSp = nameStr.split(",");
				if ( nameSp.length!=2 ){
					System.out.println( name + "定義ファイルのフォーマットが不正です");
					return false;
				}
				// 読み込んだデータをそれぞれのMAPに覚えさせる
				// 支店番号が数字で３桁でなければエラーを返す
				if ( nameSp[0].matches(matchesCondition) && nameStr.length()!=2 ) {
					// branchMap,commodityMapへ落とし込み
					nameMap.put(nameSp[0], nameSp[1]);
					salesMap.put(nameSp[0], 0L);
				} else {
					System.out.println(name+"定義ファイルのフォーマットが不正です");
					return false;
				}
			}
		} catch(IOException e)	{
		System.out.println(name+"定義ファイルのフォーマットが不正です");

		return false;
		} finally {
			try {
				br.close();
			} catch ( IOException e ) {
				// TODO 自動生成された catch ブロック
				System.out.println("予期せぬエラーが発生しました");
				return false;
			}
		} return true;
	}
}