package com.seekweb4.chat.api.utils;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

@SuppressWarnings("deprecation")
public class ExportExcelUtil {

	/*public static void export(ProductOrder order, String fileName, HttpServletResponse response) {
		try {
			// 1.创建工作簿
			HSSFWorkbook workbook = new HSSFWorkbook();
			// 1.1创建合并单元格对象
			// 标题
			CellRangeAddress callRangeAddress = new CellRangeAddress(0, 0, 0, 4);// 起始行,结束行,起始列,结束列
			// 订单编号
			//CellRangeAddress callRangeAddress1 = new CellRangeAddress(1, 1, 0, 0);// 起始行,结束行,起始列,结束列
			// 下单时间
			CellRangeAddress callRangeAddress20 = new CellRangeAddress(1, 1, 1, 2);// 起始行,结束行,起始列,结束列
			// 付款时间
			CellRangeAddress callRangeAddress21 = new CellRangeAddress(1, 1, 3, 4);// 起始行,结束行,起始列,结束列
			// 收货人
			//CellRangeAddress callRangeAddress22 = new CellRangeAddress(2, 2, 0, 0);// 起始行,结束行,起始列,结束列
			// 收货电话
			CellRangeAddress callRangeAddress23 = new CellRangeAddress(2, 2, 1, 4);// 起始行,结束行,起始列,结束列
			// 收货地址
			CellRangeAddress callRangeAddress24 = new CellRangeAddress(3, 3, 0, 4);// 起始行,结束行,起始列,结束列

			// 表头
			//CellRangeAddress callRangeAddress31 = new CellRangeAddress(4, 4, 0, 0);// 起始行,结束行,起始列,结束列
			//CellRangeAddress callRangeAddress32 = new CellRangeAddress(4, 4, 1, 1);// 起始行,结束行,起始列,结束列
			//CellRangeAddress callRangeAddress33 = new CellRangeAddress(4, 4, 2, 2);// 起始行,结束行,起始列,结束列
			//CellRangeAddress callRangeAddress34 = new CellRangeAddress(4, 4, 3, 3);// 起始行,结束行,起始列,结束列
			//CellRangeAddress callRangeAddress35 = new CellRangeAddress(4, 4, 4, 4);// 起始行,结束行,起始列,结束列

			List<ProductOrderItem> itemList = order.getProductOrderItemList();
			// 金额
			CellRangeAddress callRangeAddressnumber1 = new CellRangeAddress(itemList.size() + 5, itemList.size() + 5, 0, 4);// 起始行,结束行,起始列,结束列

			// 备注
			CellRangeAddress callRangeAddressPersion1 = new CellRangeAddress(itemList.size() + 6, itemList.size() + 6, 0, 4);// 起始行,结束行,起始列,结束列

			// 样式
			HSSFCellStyle headStyle = createCellStyle(workbook, (short) 13, true, true);
			HSSFCellStyle erStyle = createCellStyle(workbook, (short) 10, false, false);
			HSSFCellStyle sanStyle = createCellStyle(workbook, (short) 10, false, false);
			// 表头样式
			HSSFCellStyle colStyle = createCellStyle(workbook, (short) 10, true, true);
			colStyle.setBorderBottom(BorderStyle.THIN);
			colStyle.setBorderLeft(BorderStyle.THIN);//左边框
			colStyle.setBorderTop(BorderStyle.THIN);//上边框
			colStyle.setBorderRight(BorderStyle.THIN);//右边框
			// 内容样式
			HSSFCellStyle cellStyle = createCellStyle(workbook, (short) 10, false, true);
			cellStyle.setBorderBottom(BorderStyle.THIN); //下边框
			cellStyle.setBorderLeft(BorderStyle.THIN);//左边框
			cellStyle.setBorderTop(BorderStyle.THIN);//上边框
			cellStyle.setBorderRight(BorderStyle.THIN);//右边框
			// 2.创建工作表
			HSSFSheet sheet = workbook.createSheet("订单");
			// 2.1加载合并单元格对象
			sheet.addMergedRegion(callRangeAddress);
			//sheet.addMergedRegion(callRangeAddress1);
			sheet.addMergedRegion(callRangeAddress20);
			sheet.addMergedRegion(callRangeAddress21);
			//sheet.addMergedRegion(callRangeAddress22);
			sheet.addMergedRegion(callRangeAddress23);
			sheet.addMergedRegion(callRangeAddress24);
			//sheet.addMergedRegion(callRangeAddress31);
			//sheet.addMergedRegion(callRangeAddress32);
			//sheet.addMergedRegion(callRangeAddress33);
			//sheet.addMergedRegion(callRangeAddress34);
			//sheet.addMergedRegion(callRangeAddress35);
			sheet.addMergedRegion(callRangeAddressnumber1);
			sheet.addMergedRegion(callRangeAddressPersion1);
			// 设置默认列宽
			//sheet.setDefaultColumnWidth(15);
			sheet.setColumnWidth(0, 9000);
			sheet.setColumnWidth(1, 5300);
			sheet.setColumnWidth(2, 1720);
			sheet.setColumnWidth(3, 3500);
			sheet.setColumnWidth(4, 3500);
			// 3.创建行
			// 3.1创建头标题行;并且设置头标题
			HSSFRow row = sheet.createRow(0);
			row.setHeightInPoints(30);
			HSSFCell cell = row.createCell(0);
			// 加载单元格样式
			cell.setCellStyle(headStyle);
			cell.setCellValue("订单明细");

			HSSFRow rower = sheet.createRow(1);
			rower.setHeightInPoints(30);
			HSSFCell celler = rower.createCell(0);
			// 加载单元格样式
			celler.setCellStyle(erStyle);
			celler.setCellValue("订单号：" + order.getId());
			
			HSSFCell celler2 = rower.createCell(1);
			// 加载单元格样式
			celler2.setCellStyle(erStyle);
			celler2.setCellValue("下单时间：" + DateUtils.formatDateTime(order.getCreateDate()));
			
			HSSFCell celler3 = rower.createCell(3);
			// 加载单元格样式
			celler3.setCellStyle(erStyle);
			celler3.setCellValue("付款时间：" + (order.getPayDate() != null ? DateUtils.formatDateTime(order.getPayDate()) : ""));

			HSSFRow rowsan = sheet.createRow(2);
			rowsan.setHeightInPoints(30);
			HSSFCell cellsan = rowsan.createCell(0);
			HSSFCell cellsan1 = rowsan.createCell(1);
			// 加载单元格样式
			cellsan.setCellStyle(sanStyle);
			cellsan.setCellValue("收货人：" + order.getUsername());
			cellsan1.setCellStyle(sanStyle);
			cellsan1.setCellValue("联系电话：" + order.getPhone());
			
			HSSFRow rowsi = sheet.createRow(3);
			rowsi.setHeightInPoints(30);
			HSSFCell cellsi = rowsi.createCell(0);
			// 加载单元格样式
			cellsi.setCellStyle(sanStyle);
			cellsi.setCellValue("收货地址：" + order.getAddress());

			// 3.2创建列标题;并且设置列标题
			HSSFRow row2 = sheet.createRow(4);
			row2.setHeightInPoints(20);
			String[] titles = { "商品名称", "规格", "数量", "单价（元）", "小计（元）"};// ""为占位字符串
			for (int i = 0; i < titles.length; i++) {
				HSSFCell cell2 = row2.createCell(i);
				// 加载单元格样式
				cell2.setCellStyle(colStyle);
				cell2.setCellValue(titles[i]);
			}

			// 4.操作单元格;将列表写入excel
			if (itemList != null) {
				itemList.sort((ProductOrderItem item1, ProductOrderItem item2) -> item1.getSkuname().compareTo(item2.getSkuname()));
				Map<String, Integer> productIds = Maps.newHashMap();
				for (ProductOrderItem item : itemList) {
					if (productIds.containsKey(item.getProductId())) {
						Integer count = productIds.get(item.getProductId());
						productIds.put(item.getProductId(), count + 1);
					} else {
						productIds.put(item.getProductId(), 1);
					}
				}
				Set<Entry<String,Integer>> entrySet = productIds.entrySet();
				int rowNum = 5;// 行号
				int rowNum2 = 5;// 行号
				for (Entry<String, Integer> entry : entrySet) {
					String key = entry.getKey();
					int count = entry.getValue();
					CellRangeAddress callRangeAddress6 = new CellRangeAddress(rowNum, rowNum + count - 1, 0, 0);// 起始行,结束行,起始列,结束列
					//sheet.addMergedRegion(callRangeAddress6);
					
					for (int j = 0; j < itemList.size(); j++) {
						if (key.equals(itemList.get(j).getProductId())) {
							// 创建数据行,前面有两行,头标题行和列标题行
							//HSSFRow row3 = sheet.createRow(j + 5);
							HSSFRow row3 = sheet.createRow(rowNum2);
							row3.setHeightInPoints(30);
							HSSFCell cell0 = row3.createCell(0);
							cell0.setCellStyle(cellStyle);
							cell0.setCellValue(itemList.get(j).getProductTitle());
							
							HSSFCell cell1 = row3.createCell(1);
							cell1.setCellStyle(cellStyle);
							cell1.setCellValue(itemList.get(j).getSkuname());
							
							HSSFCell cell2 = row3.createCell(2);
							cell2.setCellStyle(cellStyle);
							cell2.setCellValue(itemList.get(j).getQty());
							
							HSSFCell cell3 = row3.createCell(3);
							cell3.setCellStyle(cellStyle);
							cell3.setCellValue(itemList.get(j).getPrice());
							
							HSSFCell cell4 = row3.createCell(4);
							cell4.setCellStyle(cellStyle);
							cell4.setCellValue(new BigDecimal(itemList.get(j).getPrice()).multiply(new BigDecimal(itemList.get(j).getQty())).toPlainString());
							
							rowNum2++;
						}
					}
					rowNum = rowNum + count;
				}
				
			}

			HSSFRow rownumber = sheet.createRow(itemList.size() + 5);
			rownumber.setHeightInPoints(30);
			HSSFCell cellnumber = rownumber.createCell(0);
			// 加载单元格样式
			cellnumber.setCellStyle(sanStyle);
			cellnumber.setCellValue("总数量："+order.getQty()+"   总金额："+order.getPrice()+"元   优惠金额："+order.getDiscount()+"元   安装费："+order.getCost()+"元   运费："+order.getFreight()+"元   实付金额：" + order.getAmount() + "元");

			HSSFRow rowpersion = sheet.createRow(itemList.size() + 6);
			rowpersion.setHeightInPoints(40);
			HSSFCell cellpersion = rowpersion.createCell(0);

			// 加载单元格样式
			cellpersion.setCellStyle(sanStyle);
			cellpersion.setCellValue("备注：" + (StringUtils.isNotBlank(order.getRemarks()) ? order.getRemarks() : ""));
			// 5.输出
			//workbook.write(fout);
			
			response.reset();
	        response.setContentType("application/octet-stream; charset=utf-8");
			response.setHeader("Content-Disposition", URLEncoder.encode(fileName, "UTF8"));
			workbook.write(response.getOutputStream());
			//workbook.close();
			//out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	/**
	 * 
	 * @param workbook
	 * @param fontsize
	 * @return 单元格样式
	 */
	private static HSSFCellStyle createCellStyle(HSSFWorkbook workbook, short fontsize, boolean flag, boolean flag1) {
		HSSFCellStyle style = workbook.createCellStyle();
		// 是否水平居中
		if (flag1) {
			style.setAlignment(HorizontalAlignment.CENTER);// 水平居中
		}

		style.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直居中
		// 创建字体
		HSSFFont font = workbook.createFont();
		// 是否加粗字体
		if (flag) {
			font.setBold(true);
		}
		font.setFontHeightInPoints(fontsize);
		// 加载字体
		style.setFont(font);
		style.setWrapText(true);
		return style;
	}
	
}
