package jp.co.alh.watanabe_yasunori.売上集計;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;public class uriagesyuukei {
	public static void main (String[] args){


//店舗定義用MAPと商品定義用MAP
		HashMap <String,String> branchmap = new HashMap<String,String>();
		HashMap <String,String> commoditymap = new HashMap<String,String>();
		HashMap <String,String> branchsalesmap = new HashMap<String,String>();
		HashMap <String,String> commoditysalesmap = new HashMap<String,String>();



//branchデータファイルを読み込み
		try{
			File branchfile = new File(args[0],"branch.lst");

			FileReader branchfr = new FileReader(branchfile);

			BufferedReader branchbr = new BufferedReader(branchfr);


			//エラー処理

			String brancherror="支店定義ファイルのフォーマットが不正です";

			//データファイルを文字列に
			String branch_s;


			//繰り返してデータファイルを一行ずつ読み込む
			while((branch_s = branchbr.readLine()) !=  null){

				//System.out.println(s);
				String branchstr = branch_s;
				String [] branch_sp = branchstr.split(",");
				//読み込んだデータをそれぞれのMAPに覚えさせる
				branchmap.put(branch_sp[0],branch_sp[1]);


				//読み込んだ支店番号を数字かどうか判断する(作成中)

				/*if(.matches("[0-9]")){
					System.out.println(branch_sp[0]+"支店定義ファイルは数字です");

				}else{
					System.out.println("支店定義ファイルは数字ではありません");
				}*/

			}

			branchbr.close();

		}
		catch(IOException e){
			System.out.println("支店定義ファイルが存在しません");


		}
		//上記の処理を商品定義にて
		try{
			File commodityfile = new File(args[0],"commodity.lst");
			FileReader commodityfr = new FileReader(commodityfile);
			BufferedReader commoditybr = new BufferedReader(commodityfr);

			String commodity_s;

			while((commodity_s = commoditybr.readLine()) !=  null){

				String commoditystr = commodity_s;
				String [] commodity_sp = commoditystr.split(",");
				//読み込んだデータをそれぞれのMAPに覚えさせる
				commoditymap.put(commodity_sp[0],commodity_sp[1]);
				//System.out.println(commodity_sp[0]);
			}

			commoditybr.close();


		}
		catch(IOException e){
			System.out.println("商品定義ファイルが存在しません");

		}

		//レジストリ内のファイルデータ読み込み
		try{
			 File files1 = new File(args[0]);
			// File extraction = new File(args[0]);
			 String flist[] = files1.list();
			 ArrayList<String> extraction =new ArrayList<String>();
			 //String extractionList[] = extraction.list();
			 for(int i = 0;i< flist.length;i++){
				// System.out.println(flist[i]);
				 //読み込んだレジストリから『数字八桁』かつ『.rsd』のファイルを選別しリストに当て込む
				 if(flist[i].matches("\\d{8}.rcd$")){
					 extraction.add(flist[i]);

				 }
			 }
			 //System.out.println(extraction.get(2));

			 String sales_s;
			 ArrayList<String> branchAllocation = new ArrayList<String>();
			 int i =0;
			 for(i = 0; i < extraction.size();i++){
				 //抽出した売り上げデータからファイルを読み込む
				 File salesfile = new File(args[0],extraction.get(i));
				 FileReader salesfr = new FileReader(salesfile);
				 BufferedReader salesbr = new BufferedReader(salesfr);

				 //System.out.print(salesbr);
				 //System.out.println(salesbr);


				 //支店番号と売り上げ金額の当て込み
				 while((sales_s = salesbr.readLine()) != null){
			 		//System.out.println(sales_s);
					 branchAllocation.add(sales_s);

				 }

				 //System.out.println(branchAllocation.get(b));




				 //String salesstr = sales_s;
				 //String [] sales_sp = salesstr.split("",1);
				 //支店番号と

				 salesbr.close();

			 }
			// System.out.println(branchsalesmap.get(001));
			 //System.out.println();













			/*File salesfile = new File(args[0],"");
			FileReader salesfr = new FileReader(salesfile);
			BufferedReader salesbr = new BufferedReader(salesfr);

			String sales_s;

			while((sales_s = salesbr.readLine()) !=  null){

				String salesstr = sales_s;
				String [] sales_sp = salesstr.split(",");
				//読み込んだデータをそれぞれのMAPに覚えさせる
				salesmap.put(sales_sp[0],sales_sp[1]);
				//System.out.println(commodity_sp[0]);
			}*/


		}
		catch(Exception e){
				System.out.println(e);
		}




		//MAPに入っているデータを参照する
/*
		System.out.println("支店番号001番は"+branchmap.get("001"));
		System.out.println("支店番号002番は"+branchmap.get("002"));
		System.out.println("支店番号003番は"+branchmap.get("003"));
		System.out.println("支店番号004番は"+branchmap.get("004"));
		System.out.println("支店番号005番は"+branchmap.get("005"));
		System.out.println("商品番号SFT00001は"+commoditymap.get("SFT00001"));
		System.out.println("商品番号SFT00002は"+commoditymap.get("SFT00002"));
		System.out.println(branchmap.entrySet());
		*/
	}

}
