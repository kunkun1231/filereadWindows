package cn.kun.cn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingWorker;

public class ReadFindFile {

	private static SimpleDateFormat format = null;
	private static SimpleDateFormat formatHouse = null;
	private static boolean bool = false;
	private static boolean boolTwo = true;

	private static boolean RUN_SYSTEM = true;

	public static int findReadAndPrint(String f, String find, int line,
			boolean isPrint, String path) throws Exception {
		File file = new File(f);
		List<Long> findDataList = findDataFile(file, find);
		int size = findDataList.size();
		System.out.println("正在出文件");
		if (isPrint) {
			if (isPrint && findDataList.size() > 0) {

				for (int i = 0; i < findDataList.size(); i++) {
					File outFile = new File(path + "/" + i + ".txt");

					writeFile(file, outFile, findDataList.get(i), line);
					// String st = "一共:" + size + "/当前:" + i;

				}
			}
		}
		return size;
	}

	public static List<Long> findDataFile(File file, String find)
			throws Exception {
		System.out.println("正在读取文件");
		List<Long> line = new ArrayList<Long>();
		BufferedReader br = null;
		br = setBrCoding(file);
		long count = 1;
		String st = null;
		try {
		
			while ((st = br.readLine()) != null) {
				
				if (st.contains(find) || st.indexOf(find) != -1) {
					line.add(count);
				}
				
				count++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("读取完成");
		br.close();
		return line;
	}

	private static BufferedReader setBrCoding(File file)
			throws UnsupportedEncodingException, FileNotFoundException {
		BufferedReader br;
		if (file.getName().contains("_i")) {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file), "UTF-8"));
		} else {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file), "GBK"));
		}
		return br;
	}

	public static void writeFile(File file, File outFile, Long number, int h)
			throws Exception {
		BufferedReader br = null;
		BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
		br = setBrCoding(file);
		if (number > 0) {
			long count = 1;
			int hlaf = h / 2;
			String st = null;
			while ((st = br.readLine()) != null) {

				if (count >= number - hlaf && count <= number + hlaf) {
					bw.write(count + "行" + st + "\r\n");
					bw.flush();
				}
				count++;
			}
		} else {
			bw.write("没有找到内容");
			bw.flush();
		}

		bw.close();
		br.close();
	}

	/**
	 * 根据时间查询 文件
	 * 
	 * @param file
	 *            源文件
	 * @param start
	 *            开始时间
	 * @param end
	 *            结束时间
	 * @param path
	 *            结果文件位置
	 * @param dj
	 *            级别 DEBUG INFO ERROR
	 * @throws Exception
	 */
	public static int findDataFileDate(String soureFile, String start,
			String end, String path, String dj) throws Exception {
		File file = null;
		InputStreamReader isr = null;
		boolean fileFormat;
		if (soureFile.contains("cp")) {
			fileFormat = true;
			isr = new InputStreamReader(
					new FileInputStream(new File(soureFile)), "GBK");
		} else {
			fileFormat = false;
			isr = new InputStreamReader(
					new FileInputStream(new File(soureFile)), "UTF-8");
		}
		BufferedReader br = new BufferedReader(isr);
		format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatHouse = new SimpleDateFormat("HH:mm");

		BufferedWriter bw = null;
		Date startDate = formatHouse.parse(start);
		Date endDate = formatHouse.parse(end);
		if (endDate.getTime() < startDate.getTime()) {
			System.out.println("时间输入错误");
			return -1;
		}

		int i = 0;
		while (br.ready()) {
			System.out.println("正在读取...");
			if (bw == null || file.length() > 1024 * 1024 * 50) {
				file = new File(path + "/fileOut" + i++ + ".txt");
				bw = new BufferedWriter(new FileWriter(file));
			}
			String st = br.readLine();
			// System.out.println(st);
			try {

				if (fileFormat) {
					cpStart(st, startDate, endDate, bw, dj);
				} else {
					creatDateFile(dj, bw, startDate, endDate, st);
				}
				if (!boolTwo) {
					break;
				}
			} catch (Exception e) {
				if (bool) {
					bw.write(st + "\r\n");
					bw.flush();
				}
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		bw.close();
		br.close();
		System.out.println("文件生成完成!地址--->" + path);
		return 0;
	}

	private static void creatDateFile(String dj, BufferedWriter bw,
			Date startDate, Date endDate, String st) throws ParseException,
			IOException {
		if (st.contains("INFO")) {
			String[] split = st.split(",");
			if (split != null && split.length > 0) {
				String substring = split[0].substring(split[0].length() - 19,
						split[0].length());
				Date parse = format.parse(substring);
				String xs = formatHouse.format(parse);
				long readDate = formatHouse.parse(xs).getTime();
				writeFileMothod(st, startDate, endDate, bw, dj, readDate);
			}
		} else {
			if (bool) {
				bw.write(st + "\r\n");
				bw.flush();
			}
		}
	}

	public static void cpStart(String st, Date startDate, Date endDate,
			BufferedWriter bw, String dj) throws Exception {
		if (st.contains("method")) {
			// System.out.println(st);
			String[] split = st.split(",");
			if (split != null && split.length > 0) {

				String sDate = split[0];
				String[] dateK = sDate.split("]");

				if (dateK != null && dateK.length > 1) {
					String date = dateK[1].substring(1, dateK[1].length());
					Date parse = format.parse(date);
					String xs = formatHouse.format(parse);
					long readDate = formatHouse.parse(xs).getTime();
					writeFileMothod(st, startDate, endDate, bw, dj, readDate);

				}

			}

		} else {
			if (bool) {
				bw.write(st + "\r\n");
				bw.flush();
			}

		}
	}

	private static void writeFileMothod(String st, Date startDate,
			Date endDate, BufferedWriter bw, String dj, long readDate)
			throws IOException {
		if (readDate >= startDate.getTime() && readDate <= endDate.getTime()) {
			bool = true;
			String info = st + "\r\n";
			if (!"".equals(dj) && st.contains(dj)) {
				info = st + "\r\n";
			}
			bw.write(info);
			bw.flush();
		}
		// System.out.println(readDate + "+++" +endDate.getTime());
		if (readDate > endDate.getTime()) {
			bool = false;
			boolTwo = false;
		}
	}

	public static void swingWorkDate(final String soureFile,
			final String start, final String end, final String path,
			final String dj, final JLabel ruslutlabel, final JButton enter,
			final JButton cancel) {

		new SwingWorker<String, String>() {

			@Override
			protected String doInBackground() throws Exception {

				File file = null;
				InputStreamReader isr = null;
				boolean fileFormat;
				if (soureFile.contains("cp")) {
					fileFormat = true;
					isr = new InputStreamReader(new FileInputStream(new File(
							soureFile)), "GBK");
				} else {
					fileFormat = false;
					isr = new InputStreamReader(new FileInputStream(new File(
							soureFile)), "UTF-8");
				}
				BufferedReader br = new BufferedReader(isr);
				format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				formatHouse = new SimpleDateFormat("HH:mm");

				BufferedWriter bw = null;
				String regex = "[0-9]{2}:[0-9]{2}";
				if (!start.matches(regex) || !end.matches(regex)) {
					onFind(enter, cancel);
					ruslutlabel.setText("时间输入错误(HH:mm)");
					return null;
				}
				Date startDate = formatHouse.parse(start);
				Date endDate = formatHouse.parse(end);
				if (endDate.getTime() < startDate.getTime()) {
					// System.out.println("时间输入错误");
					ruslutlabel.setText("时间输入错误,开始时间不能大于结束时间");
					onFind(enter, cancel);
					return null;
				}
				offFind(enter, cancel);
				int i = 0;
				publish("正在读取...");
				RUN_SYSTEM = true;
				boolTwo = true;
				String st = null;
				while ((st = br.readLine()) != null) {
					if (!RUN_SYSTEM) {
						publish("停止文件");
						onFind(enter, cancel);
						if (bw != null) {
							bw.close();
						}
						if (br != null) {
							br.close();
						}
						break;
					}
					// System.out.println("正在读取...");
					if (bw == null || file.length() > 1024 * 1024 * 50) {
						file = new File(path + "/fileOut" + i++ + ".txt");
						bw = new BufferedWriter(new FileWriter(file));
					}

					// System.out.println(st);
					try {
						if (bool) {
							publish("正在打印文件");
						} else {
							publish("正在扫描文件");
						}
						if (fileFormat) {
							cpStart(st, startDate, endDate, bw, dj);
						} else {
							creatDateFile(dj, bw, startDate, endDate, st);
						}
						if (!boolTwo) {
							ruslutlabel.setText("文件打印完成");
							System.out.println("完成");
							break;
						}
					} catch (Exception e) {
						if (bool) {
							bw.write(st + "\r\n");
							bw.flush();
						}
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				bw.close();
				br.close();
				onFind(enter, cancel);
				publish("文件生成完成!地址--->" + path);
				System.out.println("文件生成完成!地址--->" + path);
				return null;
			}

			protected void process(List<String> chunks) {
				ruslutlabel.setText(chunks.get(0));
			};

		}.execute();
	}

	/**
	 * 关键字
	 * 
	 * @param f
	 * @param find
	 * @param line
	 * @param isPrint
	 * @param path
	 * @param ruslutlabel
	 */
	public static void findSwingWork(final String f, final String find,
			final int line, final boolean isPrint, final String path,
			final JLabel ruslutlabel, final JButton enter, final JButton cancel) {
		RUN_SYSTEM = true;
		final File file = new File(f);
		
		if (file.length() > 1024 * 1024 * 1024 * 2) {
			double fileSize = file.length() / 1024.0 / 1024.0 / 1024.0;
			System.out.println("文件大小:" + fileSize);

		}

		new SwingWorker<String, String>() {

			@Override
			protected String doInBackground() throws Exception {
				// TODO Auto-generated method stub

				filewWord(find, line, isPrint, path, enter, cancel, file);
				return null;
			}

			private void filewWord(final String find, final int line,
					final boolean isPrint, final String path,
					final JButton enter, final JButton cancel, final File file)
					throws Exception {
				List<Long> findDataList = findDataFile(file, find);
				int size = findDataList.size();
				offFind(enter, cancel);
				if (isPrint) {
					System.out.println("正在出文件!");
					if (isPrint && findDataList.size() > 0) {

						for (int i = 0; i < findDataList.size(); i++) {
							if (!RUN_SYSTEM) {
								onFind(enter, cancel);
								publish("停止文件");
								break;
							}
							String st = "一共:" + size + "/当前:" + i;
							System.out.println(st);
							publish(st);
							File outFile = new File(path + "/" + i + ".txt");
							writeFile(file, outFile, findDataList.get(i), line);

						}
						onFind(enter, cancel);
						publish("完成");
					} else {
						onFind(enter, cancel);
						publish("关键字一共:" + size);
					}
				} else {
					onFind(enter, cancel);
					publish("不出文件，关键字一共:" + size);
				}
			}

			@Override
			protected void process(List<String> chunks) {
				// TODO Auto-generated method stub
				// System.out.println("+");
				ruslutlabel.setText(chunks.get(0));
				super.process(chunks);
			}

		}.execute();

	}

	// 打开
	public static void onFind(JButton enter, JButton cancel) {
		enter.setVisible(true);
		cancel.setVisible(false);
	}

	// 关闭
	public static void offFind(JButton enter, JButton cancel) {
		enter.setVisible(false);
		cancel.setVisible(true);
	}

	public static void SystemStop() {
		RUN_SYSTEM = false;
	}

}
