import time

import mysql.connector


class lazy_import:
    conn = mysql.connector.connect(
        user='root',
        password='1111',
        host='127.0.0.1',
        database='k2_tech_store',
        auth_plugin='mysql_native_password'
    )
    cur = conn.cursor()
    now = time.strftime('%Y-%m-%d %H:%M:%S')
    description_demo = '''<p>iPhone 11 được cho là phiên bản nâng cấp của iPhone XR ra mắt năm ngoái. Trong khi 
            đó, iPhone 11 Pro là phiên bản kế thừa của iPhone XS và được trang bị màn hình OLED. Cuối cùng, iPhone 11 Pro 
            Max sẽ là phiên bản nâng cấp của iPhone XS Max.&nbsp;</p><p><a 
            href="https://vnn-imgs-f.vgcloud.vn/2019/09/10/14/iphone-11-iphone-11-pro-cau-hinh-gia-ban-va-tat-ca-thong
            -tin-ruoc-ngay-ra-mat-1.jpg" target="_blank" style="color: rgb(151, 25, 40); background-color: initial;"><img 
            src="https://vnn-imgs-f.vgcloud.vn/2019/09/10/14/iphone-11-iphone-11-pro-cau-hinh-gia-ban-va-tat-ca-thong-tin
            -ruoc-ngay-ra-mat-1.jpg" alt="iPhone 11, iPhone 11 Pro: Cấu hình, giá bán và tất cả thông tin trước giờ 
            G"></a><em>Nhiều dự đoán cho thấy sẽ có ít nhất 3 mẫu điện thoại được Apple ra mắt trong đêm 
            nay.&nbsp;</em></p><p>Những thông tin ban đầu cho thấy, iPhone 11 được trang bị màn hình LCD kích thước 6,
            1 inch (độ phân giải 1792x828). Ở 2 phiên bản còn lại, máy sẽ có màn hình kích thước 5.8 inch (độ phân giải 
            2436x1125) với iPhone 11 Pro và 6.5 inch (độ phân giải 2436x1125) với iPhone 11 Pro Max.&nbsp;</p><p>Đáng chú 
            ý khi mới đây, nhiều nguồn tin cho biết, Apple có thể sẽ ra mắt một phiên bản iPhone thứ 4 có kết nối 
            5G.</p><p><span style="background-color: initial;">Cấu hình bộ 3 iPhone mới&nbsp;</span></p><p>Theo thông tin 
            được đồn đại từ trước đó, iPhone 11 được trang bị chip A13 với 4GB RAM. Camera trước của iPhone 11 có độ phân 
            giải 12 MP, hỗ trợ Face ID giúp người dùng có thể mở khóa iPhone ở góc camera rộng hơn. Nhờ vậy, mẫu iPhone 
            mới được cho là có khả năng mở khóa ngay cả khi người dùng đặt máy trên mặt bàn.</p><p><a 
            href="https://vnn-imgs-f.vgcloud.vn/2019/09/10/14/iphone-11-iphone-11-pro-cau-hinh-gia-ban-va-tat-ca-thong
            -tin-ruoc-ngay-ra-mat-8.jpg" target="_blank" style="color: rgb(151, 25, 40); background-color: initial;"><img 
            src="https://vnn-imgs-f.vgcloud.vn/2019/09/10/14/iphone-11-iphone-11-pro-cau-hinh-gia-ban-va-tat-ca-thong-tin
            -ruoc-ngay-ra-mat-8.jpg" alt="iPhone 11, iPhone 11 Pro: Cấu hình, giá bán và tất cả thông tin trước giờ 
            G"></a><em>Cấu hình dự kiến của iPhone 11, iPhone 11 Pro và iPhone 11 Pro Max.&nbsp;</em></p><p>iPhone 11 sở 
            hữu viên pin dung lượng 3.110 mAh, một sự nâng cấp nhẹ từ viên pin 2.942 mAh của iPhone XR ra mắt hồi năm 
            ngoái.&nbsp;</p><p>Với iPhone 11 Pro, máy sở hữu chip A13 với các tính năng tương tự như iPhone 11. Điểm khác 
            biệt nằm ở chỗ, iPhone 11 Pro sẽ có bộ nhớ RAMi 6GB. Bên cạnh đó, camera của iPhone 11 Pro cũng sẽ ưu việt 
            hơn hẳn dòng máy dưới. Pin của iPhone 11 Pro cũng lớn hơn với dung lượng 3.190 mAh.</p><p>iPhone 11 Pro Max 
            là một phiên bản nâng cấp về kích cỡ của iPhone 11 Pro. Ngoài màn hình lớn hơn, máy sẽ có dung lượng pin 
            3.174 mAh. Cả 3 mẫu iPhone mới của năm nay đều sẽ được loại bỏ tính năng 3D Touch.&nbsp;</p><p><span 
            style="background-color: initial;">Camera: Nâng cấp đáng kể nhất của iPhone 2019</span></p><p>Thông tin rò rỉ 
            từ chuỗi cung ứng của Apple cho biết, Apple sẽ trang bị hệ thống camera 3 ống kính cho mẫu iPhone của năm 
            nay. Hệ thống camera 3 ống kính sẽ chỉ có trên phiên bản iPhone 11 Pro và iPhone 11 Pro Max cao cấp 
            nhất.</p><p><a href="https://vnn-imgs-f.vgcloud.vn/2019/09/10/14/iphone-11-iphone-11-pro-cau-hinh-gia-ban-va
            -tat-ca-thong-tin-ruoc-ngay-ra-mat-6.jpg" target="_blank" style="color: rgb(151, 25, 40); background-color: 
            initial;"><img src="https://vnn-imgs-f.vgcloud.vn/2019/09/10/14/iphone-11-iphone-11-pro-cau-hinh-gia-ban-va
            -tat-ca-thong-tin-ruoc-ngay-ra-mat-6.jpg" alt="iPhone 11, iPhone 11 Pro: Cấu hình, giá bán và tất cả thông 
            tin trước giờ G"></a><em>iPhone 11 Pro và iPhone 11 Pro Max sẽ có 3 camera được thiết kế dạng khung 
            vuông.&nbsp;</em></p><p>Cụm 3 camera này được bố trí trong một khung hình vuông. Camera mới của iPhone 2019 
            lồi lên hẳn so với phần thân của máy. Cách thiết kế kỳ quặc này không nhận được đánh giá tích cực từ các fan 
            của Táo khuyết. Nhiều người dùng còn khẳng định, họ sẽ không bao giờ chấp nhận thiết kế này.</p><p><a 
            href="https://vnn-imgs-f.vgcloud.vn/2019/09/10/14/iphone-11-iphone-11-pro-cau-hinh-gia-ban-va-tat-ca-thong
            -tin-ruoc-ngay-ra-mat-3.jpg" target="_blank" style="color: rgb(151, 25, 40); background-color: initial;"><img 
            src="https://vnn-imgs-f.vgcloud.vn/2019/09/10/14/iphone-11-iphone-11-pro-cau-hinh-gia-ban-va-tat-ca-thong-tin
            -ruoc-ngay-ra-mat-3.jpg" alt="iPhone 11, iPhone 11 Pro: Cấu hình, giá bán và tất cả thông tin trước giờ 
            G"></a><em>Mẫu iPhone 11 với giá bán thấp hơn sẽ không sở hữu camera "khủng" như bộ đôi sản phẩm siêu cao 
            cấp.&nbsp;</em></p><p><span style="background-color: initial;">Tính năng chống rơi vỡ, đập đất không 
            hỏng</span></p><p>Thông tin mới nhất cho thấy, iPhone 11 sẽ có thiết kế tương tự với mẫu iPhone hiện tại. 
            Điểm khác biệt đáng kể giữa 2 máy nằm ở tính năng chống rơi vỡ của mẫu điện thoại này.&nbsp;</p><p>Theo 
            Bloomberg, ngoài khả năng chống vỡ, Apple sẽ tăng cường thêm khả năng chống nước cho iPhone 11. Nhờ vậy, 
            iPhone mới có thể nằm im dưới nước trong một khoảng thời gian dài hơn 30 phút.&nbsp;</p><p><span 
            style="background-color: initial;">Bút cảm ứng Apple Pencil</span></p><p>Apple Pencil vốn chỉ có thể sử dụng 
            với một vài mẫu iPad đời mới nhất của Apple. Tuy nhiên, những thông tin mới nhất cho thấy chiếc bút này sẽ 
            được tích hợp để sử dụng trên iPhone của năm 2019.&nbsp;</p><p><a 
            href="https://vnn-imgs-f.vgcloud.vn/2019/09/10/14/iphone-11-iphone-11-pro-cau-hinh-gia-ban-va-tat-ca-thong
            -tin-ruoc-ngay-ra-mat.jpeg" target="_blank" style="color: rgb(151, 25, 40); background-color: initial;"><img 
            src="https://vnn-imgs-f.vgcloud.vn/2019/09/10/14/iphone-11-iphone-11-pro-cau-hinh-gia-ban-va-tat-ca-thong-tin
            -ruoc-ngay-ra-mat.jpeg" alt="iPhone 11, iPhone 11 Pro: Cấu hình, giá bán và tất cả thông tin trước giờ 
            G"></a><em>Liệu iPhone có thể sử dụng với bút cảm ứng tương tự như chiếc Galaxy Note 10?</em></p><p>Theo đó, 
            iPhone 11 sẽ không hỗ trợ bút Apple Pencil. Chiếc bút này chỉ có thể sử dụng với 2 mẫu máy cao cấp hơn là 
            iPhone 11 Pro và iPhone 11 Pro Max. Nếu điều này là chính xác, đây sẽ là một sự thay đổi lớn trong triết lý 
            phát triển sản phẩm của Apple.</p><p>Còn nhớ, hồi năm ngoái, những tin đồn cũng đã chỉ ra rằng, iPhone Xs và 
            iPhone Xs Max sẽ là những chiếc điện thoại đầu tiên hỗ trợ bút Apple Pencil. Tuy nhiên, thực tế cho thấy đây 
            vẫn là ước mơ đối với các fan của Táo khuyết.&nbsp;</p><p><span style="background-color: initial;">iPhone 11 
            sẽ có sạc 2 chiều</span></p><p>Theo nhà phân tích Ming-Chi Kuo, iPhone 11 sẽ có sạc không dây 2 chiều cho 
            phép chia sẻ năng lượng với các thiết bị khác như AirPods, Apple Watch.</p><p>Điều này có nghĩa là người dùng 
            có thể sử dụng pin iPhone để chia sẻ cho các thiết bị khác như iPhone hoặc AirPods.</p><p><a 
            href="https://vnn-imgs-f.vgcloud.vn/2019/09/10/16/iphone-11-iphone-11-pro-cau-hinh-gia-ban-va-tat-ca-thong
            -tin-ruoc-ngay-ra-mat.jpg" target="_blank" style="color: rgb(151, 25, 40); background-color: initial;"><img 
            src="https://vnn-imgs-f.vgcloud.vn/2019/09/10/16/iphone-11-iphone-11-pro-cau-hinh-gia-ban-va-tat-ca-thong-tin
            -ruoc-ngay-ra-mat.jpg" alt="iPhone 11, iPhone 11 Pro: Cấu hình, giá bán và tất cả thông tin trước giờ 
            G"></a><em>iPhone 11 sẽ có sạc không dây 2 chiều</em></p><p>Theo<em style="background-color: 
            initial;">&nbsp;Bloomberg</em>, các iPhone mới vẫn có logo Apple nhưng không còn dòng chữ "iPhone" ở mặt sau, 
            có thể sẽ được thay thế bằng vị trí giúp người dùng đặt thiết bị muốn sạc.</p><p>Nếu đúng như vậy, 
            Apple lại đi sau Samsung với tính năng sạc ngược trên Galaxy S10 được phát hành hồi tháng 3 vừa qua. S10 có 
            thể dùng để sạc pin cho Galaxy Buds và Galaxy Watch. Galaxy Note 10 ra mắt sau đó cũng có tính năng 
            này.</p><p>Cả Ming-Chi Kuo và DigiTimes (theo MacRumors) đều dự đoán rằng, iPhone 2019 sẽ có pin lớn hơn so 
            với các phiên bản tiền nhiệm, khoảng từ 2 - 20%, tùy thuộc phiên bản, cho thời lượng pin tốt hơn.</p><p><span 
            style="background-color: initial;">iPhone 11 không còn 3D Touch</span></p><p>Nhà phân tích Ming-Chi Kuo cho 
            biết, tất cả iPhone 11 sẽ không còn cảm ứng lực 3D Touch. Điều này cũng được xác nhận bởi&nbsp;<em 
            style="background-color: initial;">Wall Street Journal.</em>&nbsp;</p><p><a 
            href="https://vnn-imgs-f.vgcloud.vn/2019/09/10/16/iphone-11-iphone-11-pro-cau-hinh-gia-ban-va-tat-ca-thong
            -tin-ruoc-ngay-ra-mat-1.jpg" target="_blank" style="color: rgb(151, 25, 40); background-color: initial;"><img 
            src="https://vnn-imgs-f.vgcloud.vn/2019/09/10/16/iphone-11-iphone-11-pro-cau-hinh-gia-ban-va-tat-ca-thong-tin
            -ruoc-ngay-ra-mat-1.jpg" alt="iPhone 11, iPhone 11 Pro: Cấu hình, giá bán và tất cả thông tin trước giờ 
            G"></a><em>iPhone 11 sẽ bỏ 3D Touch</em></p><p>Các nhà phân tích của Barclay dựa trên nguồn tin từ chuỗi cung 
            ứng Apple ở châu cũng khẳng định (trích dẫn bởi&nbsp;<em style="background-color: initial;">MacRumors</em>), 
            iPhone 2019 sẽ nói lời tạm biệt với 3D Touch. Nhưng thay vào đó sẽ là công nghệ phản hồi xúc giác Haptic 
            Touch, được thử nghiệm trên iPhone XR ra mắt năm ngoái.</p><p><span style="background-color: initial;">Vi xử 
            lý A13 hiệu năng cao</span></p><p>Chip A12 Bionic đủ mạnh để cạnh tranh với các điện thoại Android ít nhất 
            trong 2 năm tới và mạnh hơn rất nhiều so với các smartphone Android cấp thấp, theo nhà phân tích Ben 
            Thompson.</p><p><a href="https://vnn-imgs-f.vgcloud.vn/2019/09/10/16/iphone-11-iphone-11-pro-cau-hinh-gia-ban
            -va-tat-ca-thong-tin-ruoc-ngay-ra-mat-2.jpg" target="_blank" style="color: rgb(151, 25, 
            40); background-color: initial;"><img 
            src="https://vnn-imgs-f.vgcloud.vn/2019/09/10/16/iphone-11-iphone-11-pro-cau-hinh-gia-ban-va-tat-ca-thong-tin
            -ruoc-ngay-ra-mat-2.jpg" alt="iPhone 11, iPhone 11 Pro: Cấu hình, giá bán và tất cả thông tin trước giờ 
            G"></a><em>iPhone 11 sẽ được trang bị chip A13 mạnh mẽ hơn</em></p><p>Tuy nhiên, Apple dường như không muốn 
            sức mạnh của iPhone chỉ dừng lại ở đó. Thông tin từ Bloomberg hồi tháng 5 cho biết, Apple đang sản xuất hàng 
            loạt vi xử lý thệ hệ tiếp theo dự kiến trang bị cho iPhone mới. Nó có thể là một biến thể của bộ xử lý A12X 
            trên iPad Pro hoặc 1 chip mới hoàn toàn được gọi là A13.</p><p><span style="background-color: initial;">Không 
            có iPhone 5G</span></p><p>Gần như chắc chắn sẽ không có iPhone 5G ra mắt trong năm nay.</p><p>Tất cả các điện 
            thoại của Apple gần đây nhất đều sử dụng modem do Intel sản xuất và dự định sẽ có modem 5G cho các thiết bị 
            vào nửa cuối năm 2019.</p><p><a href="https://vnn-imgs-f.vgcloud.vn/2019/09/10/16/iphone-11-iphone-11-pro-cau
            -hinh-gia-ban-va-tat-ca-thong-tin-ruoc-ngay-ra-mat-3.jpg" target="_blank" style="color: rgb(151, 25, 
            40); background-color: initial;"><img 
            src="https://vnn-imgs-f.vgcloud.vn/2019/09/10/16/iphone-11-iphone-11-pro-cau-hinh-gia-ban-va-tat-ca-thong-tin
            -ruoc-ngay-ra-mat-3.jpg" alt="iPhone 11, iPhone 11 Pro: Cấu hình, giá bán và tất cả thông tin trước giờ 
            G"></a><em>Không có iPhone 5G trong năm nay</em></p><p>Nhưng Intel gặp vấn đề trong việc sản xuất chip 5G cho 
            iPhone. Điều này đã khiến Apple buộc phải quay sang thỏa thuận với Qualcomm hồi tháng 4, để hãng này cung cấp 
            chip mới cho iPhone trong nhiều năm tới.</p><p>Với tình hình đó, có vẻ như Táo khuyết sẽ ra mắt iPhone 5G vào 
            năm 2020. Nhà phân tích Ming-Chi Kuo trước đó cũng dự đoán, có thể cả 3 mẫu iPhone 2020 sẽ hỗ trợ mạng 
            5G.</p><p><span style="background-color: initial;">Giá bán bộ 3 iPhone&nbsp;2019</span></p><p>Theo dự đoán 
            của Citi Research, Apple sẽ không thay đổi về cấu trúc giá bán đối với những mẫu iPhone của năm nay. Theo đó, 
            iPhone 11 sẽ có giá từ 749 USD, 999 USD là giá khởi điểm của iPhone 11 Pro. Với iPhone 11 Pro Max, 
            phiên bản mới của iPhone Xs Max sẽ có giá khởi điểm là 1.099 USD.</p><p><a 
            href="https://vnn-imgs-f.vgcloud.vn/2019/09/10/14/iphone-11-iphone-11-pro-cau-hinh-gia-ban-va-tat-ca-thong
            -tin-ruoc-ngay-ra-mat-2.jpg" target="_blank" style="color: rgb(151, 25, 40); background-color: initial;"><img 
            src="https://vnn-imgs-f.vgcloud.vn/2019/09/10/14/iphone-11-iphone-11-pro-cau-hinh-gia-ban-va-tat-ca-thong-tin
            -ruoc-ngay-ra-mat-2.jpg" alt="iPhone 11, iPhone 11 Pro: Cấu hình, giá bán và tất cả thông tin trước giờ 
            G"></a></p><p><em>Giá bán rẻ nhất dành cho iPhone 11 được dự đoán sẽ ở mức&nbsp;hơn 17 triệu 
            đồng</em></p><p>Ngoài ra, có những yếu tố mới trong năm nay như cuộc chiến thương mại Mỹ - Trung, 
            khiến các thiết bị của Apple có thể bị bổ sung thuế quan khiến giá iPhone có thể cao hơn.</p><p>Nhà phân tích 
            Ming-Chi Kuo cho rằng, Apple đã lên kế hoạch cho việc bị tăng thuế và sẽ trì hoãn việc tăng giá iPhone trong 
            năm nay, theo&nbsp;<em style="background-color: initial;">MacRumors</em>.</p><p>Một thực tế là doanh số 
            iPhone đang dần sụt giảm, điều này cũng có thể khiến Apple sẽ có những điều chỉnh. Apple cũng đã giảm giá 
            iPhone và một số sản phẩm khác tại thị trường Trung Quốc hồi đầu năm nay.</p> '''

    def main(self):
        # self.category_import()
        # self.brand_import()
        # self.product_option_import()
        # self.product_parent_import()
        # self.product_variant_import()
        self.product_variant_expert()

        self.conn.commit()
        self.conn.close()

    def execute(self, table, data_df, is_truncate=True):
        if is_truncate:
            self.cur.execute('SET FOREIGN_KEY_CHECKS = 0;')
            self.cur.execute(f'truncate table {table};')
            self.cur.execute('SET FOREIGN_KEY_CHECKS = 1;')
        for da in data_df:
            fields = list()
            data = list()
            for key, value in da.items():
                fields.append(key)
                data.append(value)
            query = f'''insert into {table}({', '.join([str(f) for f in fields])}) values ({', '.join([("'" + str(d) + "'") for d in data])})'''
            print(query)
            self.cur.execute(query)

    def get_map(self, is_option=False):
        self.cur.execute('select brand_id, code from brand')
        brand_data = self.cur.fetchall()
        brands = {code: brand_id for brand_id, code in brand_data}
        self.cur.execute('select category_id, code from category')
        category_data = self.cur.fetchall()
        categories = {code: category_id for category_id, code in category_data}
        if is_option:
            self.cur.execute('select product_option_id, value from product_option')
            product_option_data = self.cur.fetchall()
            product_options = {value: product_option_id for product_option_id, value in product_option_data}

            self.cur.execute('select name, value from product_option')
            product_option_name_data = self.cur.fetchall()
            product_option_names = {value: name for name, value in product_option_name_data}

            return brands, categories, product_options, product_option_names
        return brands, categories

    def category_import(self):
        # import category
        categories = [
            {'code': 'phone', 'name': 'Điện thoại', 'type': 1, 'status': 1, 'create_date': str(self.now),
             'create_by': 3, 'update_date': str(self.now), 'update_by': 3},
            {'code': 'headphone', 'name': 'Tai nghe', 'type': 1, 'status': 1, 'create_date': str(self.now),
             'create_by': 3, 'update_date': str(self.now), 'update_by': 3},
            {'code': 'pc', 'name': 'Máy tính', 'type': 1, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'code': 'watch', 'name': 'Đồng hồ', 'type': 1, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'code': 'hot', 'name': 'Tin hot', 'type': 0, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'code': 'highlight', 'name': 'Nổi bật', 'type': 0, 'status': 1, 'create_date': str(self.now),
             'create_by': 3, 'update_date': str(self.now), 'update_by': 3},
            {'code': 'sales', 'name': 'Khuyến mãi', 'type': 0, 'status': 1, 'create_date': str(self.now),
             'create_by': 3, 'update_date': str(self.now), 'update_by': 3},
            {'code': 'product-new', 'name': 'Sản phẩm mới', 'type': 0, 'status': 1, 'create_date': str(self.now),
             'create_by': 3, 'update_date': str(self.now), 'update_by': 3},
            {'code': 'news', 'name': 'Tin tức', 'type': 0, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
        ]
        self.execute('category', categories)

    def brand_import(self):
        # import brand
        brands = [
            {'code': 'apple', 'name': 'Apple', 'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'code': 'xiaomi', 'name': 'Xiaomi', 'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'code': 'asus', 'name': 'Asus', 'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'code': 'huawei', 'name': 'Huawei', 'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'code': 'lg', 'name': 'LG', 'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'code': 'samsaung', 'name': 'SamSung', 'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'code': 'sony', 'name': 'Sony', 'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'code': 'vinfast', 'name': 'Vinfast', 'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
        ]
        self.execute('brand', brands)

    def product_option_import(self):
        SIZE = 0
        COLOR = 1
        RELEASE = 2
        OTHER = 3
        size_option = [
            {'name': '64 GB (Memory)', 'value': 'memory-64gb', 'type': SIZE, 'status': 1, 'create_date': str(self.now),
             'create_by': 3, 'update_date': str(self.now), 'update_by': 3},
            {'name': '128 GB (Memory)', 'value': 'memory-128gb', 'type': SIZE, 'status': 1,
             'create_date': str(self.now), 'create_by': 3, 'update_date': str(self.now), 'update_by': 3},
            {'name': '256 GB (Memory)', 'value': 'memory-256gb', 'type': SIZE, 'status': 1,
             'create_date': str(self.now), 'create_by': 3, 'update_date': str(self.now), 'update_by': 3},
            {'name': '512 GB (Memory)', 'value': 'memory-512gb', 'type': SIZE, 'status': 1,
             'create_date': str(self.now), 'create_by': 3, 'update_date': str(self.now), 'update_by': 3},
            {'name': '1 TB (Memory)', 'value': 'memory-1tb', 'type': SIZE, 'status': 1, 'create_date': str(self.now),
             'create_by': 3, 'update_date': str(self.now), 'update_by': 3},
            {'name': '3 GB (Ram)', 'value': 'ram-3gb', 'type': SIZE, 'status': 1, 'create_date': str(self.now),
             'create_by': 3, 'update_date': str(self.now), 'update_by': 3},
            {'name': '4 GB (Ram)', 'value': 'ram-4gb', 'type': SIZE, 'status': 1, 'create_date': str(self.now),
             'create_by': 3, 'update_date': str(self.now), 'update_by': 3},
            {'name': '6 GB (Ram)', 'value': 'ram-6gb', 'type': SIZE, 'status': 1, 'create_date': str(self.now),
             'create_by': 3, 'update_date': str(self.now), 'update_by': 3},
            {'name': '8 GB (Ram)', 'value': 'ram-8gb', 'type': SIZE, 'status': 1, 'create_date': str(self.now),
             'create_by': 3, 'update_date': str(self.now), 'update_by': 3},
            {'name': '16 GB (Ram)', 'value': 'ram-16gb', 'type': SIZE, 'status': 1, 'create_date': str(self.now),
             'create_by': 3, 'update_date': str(self.now), 'update_by': 3},
            {'name': '32 GB (Ram)', 'value': 'ram-32gb', 'type': SIZE, 'status': 1, 'create_date': str(self.now),
             'create_by': 3, 'update_date': str(self.now), 'update_by': 3},
            {'name': '64 GB (Ram)', 'value': 'ram-64gb', 'type': SIZE, 'status': 1, 'create_date': str(self.now),
             'create_by': 3, 'update_date': str(self.now), 'update_by': 3},
            {'name': '38 mm', 'value': '38mm', 'type': SIZE, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'name': '40 mm', 'value': '40mm', 'type': SIZE, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'name': '42 mm', 'value': '42mm', 'type': SIZE, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'name': '44 mm', 'value': '44mm', 'type': SIZE, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
        ]
        color_option = [
            {'name': 'Black', 'value': 'black', 'type': COLOR, 'status': 1, 'create_date': str(self.now),
             'create_by': 3, 'update_date': str(self.now), 'update_by': 3},
            {'name': 'White', 'value': 'white', 'type': COLOR, 'status': 1, 'create_date': str(self.now),
             'create_by': 3, 'update_date': str(self.now), 'update_by': 3},
            {'name': 'Red', 'value': 'red', 'type': COLOR, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'name': 'Purple', 'value': 'purple', 'type': COLOR, 'status': 1, 'create_date': str(self.now),
             'create_by': 3, 'update_date': str(self.now), 'update_by': 3},
            {'name': 'Midnight Green', 'value': 'midnight-green', 'type': COLOR, 'status': 1, 'create_date': str(self.now),
             'create_by': 3, 'update_date': str(self.now), 'update_by': 3},
            {'name': 'Blue', 'value': 'blue', 'type': COLOR, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'name': 'Silver', 'value': 'silver', 'type': COLOR, 'status': 1, 'create_date': str(self.now),
             'create_by': 3, 'update_date': str(self.now), 'update_by': 3},
            {'name': 'Grey', 'value': 'grey', 'type': COLOR, 'status': 1, 'create_date': str(self.now),
             'create_by': 3, 'update_date': str(self.now), 'update_by': 3},
            {'name': 'Gold', 'value': 'gold', 'type': COLOR, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'name': 'Pink', 'value': 'pink', 'type': COLOR, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
        ]
        release_option = [
            {'name': '2015', 'value': '2015', 'type': RELEASE, 'status': 1, 'create_date': str(self.now),
             'create_by': 3, 'update_date': str(self.now), 'update_by': 3},
            {'name': '2016', 'value': '2016', 'type': RELEASE, 'status': 1, 'create_date': str(self.now),
             'create_by': 3, 'update_date': str(self.now), 'update_by': 3},
            {'name': '2017', 'value': '2017', 'type': RELEASE, 'status': 1, 'create_date': str(self.now),
             'create_by': 3, 'update_date': str(self.now), 'update_by': 3},
            {'name': '2018', 'value': '2018', 'type': RELEASE, 'status': 1, 'create_date': str(self.now),
             'create_by': 3, 'update_date': str(self.now), 'update_by': 3},
            {'name': '2019', 'value': '2019', 'type': RELEASE, 'status': 1, 'create_date': str(self.now),
             'create_by': 3, 'update_date': str(self.now), 'update_by': 3},
            {'name': '2020', 'value': '2020', 'type': RELEASE, 'status': 1, 'create_date': str(self.now),
             'create_by': 3, 'update_date': str(self.now), 'update_by': 3},
            {'name': '2021', 'value': '2021', 'type': RELEASE, 'status': 1, 'create_date': str(self.now),
             'create_by': 3, 'update_date': str(self.now), 'update_by': 3},
        ]
        other_option = [
            {'name': 'Xạc không dây', 'value': 'air-charge', 'type': OTHER, 'status': 1, 'create_date': str(self.now),
             'create_by': 3, 'update_date': str(self.now), 'update_by': 3},
            {'name': 'Xạc thường', 'value': 'default-charge', 'type': OTHER, 'status': 1, 'create_date': str(self.now),
             'create_by': 3, 'update_date': str(self.now), 'update_by': 3},
            {'name': 'Kim loại', 'value': 'metal', 'type': OTHER, 'status': 1, 'create_date': str(self.now),
             'create_by': 3, 'update_date': str(self.now), 'update_by': 3},
            {'name': 'Gốm', 'value': 'non-metal', 'type': OTHER, 'status': 1, 'create_date': str(self.now),
             'create_by': 3, 'update_date': str(self.now), 'update_by': 3},
            {'name': 'Có Touchbar', 'value': 'has-touch', 'type': OTHER, 'status': 1, 'create_date': str(self.now),
             'create_by': 3, 'update_date': str(self.now), 'update_by': 3},
            {'name': 'Không có Touchbar', 'value': 'non-touch', 'type': OTHER, 'status': 1,
             'create_date': str(self.now),
             'create_by': 3, 'update_date': str(self.now), 'update_by': 3},
        ]
        product_options = size_option + color_option + release_option + other_option
        self.execute('product_option', product_options)

    def product_parent_import(self):
        brands, categories = self.get_map()
        # create data
        apple_phone = [
            {'brand_id': brands.get('apple'), 'category_id': categories.get('phone'), 'name': 'iPhone 12 Pro',
             'code': 'iphone-12-pro', 'type': 0, 'description': self.description_demo, 'price': 20000000,
             'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'brand_id': brands.get('apple'), 'category_id': categories.get('phone'), 'name': 'iPhone 12',
             'code': 'iphone-12', 'type': 0, 'description': self.description_demo, 'price': 20000000,
             'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'brand_id': brands.get('apple'), 'category_id': categories.get('phone'), 'name': 'iPhone 11 Pro',
             'code': 'iphone-11-pro', 'type': 0, 'description': self.description_demo, 'price': 20000000,
             'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'brand_id': brands.get('apple'), 'category_id': categories.get('phone'), 'name': 'iPhone 11',
             'code': 'iphone-11', 'type': 0, 'description': self.description_demo, 'price': 20000000,
             'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'brand_id': brands.get('apple'), 'category_id': categories.get('phone'), 'name': 'iPhone XSR',
             'code': 'iphone-xsr', 'type': 0, 'description': self.description_demo, 'price': 20000000,
             'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'brand_id': brands.get('apple'), 'category_id': categories.get('phone'), 'name': 'iPhone XS',
             'code': 'iphone-xs', 'type': 0, 'description': self.description_demo, 'price': 20000000,
             'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
        ]
        xiaomi_phone = [
            {'brand_id': brands.get('xiaomi'), 'category_id': categories.get('phone'), 'name': 'Redmi K40 Pro',
             'code': 'redmi-k40-pro', 'type': 0, 'description': self.description_demo, 'price': 10000000,
             'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'brand_id': brands.get('xiaomi'), 'category_id': categories.get('phone'), 'name': 'Redmi K40',
             'code': 'redmi-k40', 'type': 0, 'description': self.description_demo, 'price': 10000000,
             'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'brand_id': brands.get('xiaomi'), 'category_id': categories.get('phone'), 'name': 'Redmi K30 Pro',
             'code': 'redmi-k30-pro', 'type': 0, 'description': self.description_demo, 'price': 10000000,
             'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'brand_id': brands.get('xiaomi'), 'category_id': categories.get('phone'), 'name': 'Redmi K30 5G',
             'code': 'redmi-k30', 'type': 0, 'description': self.description_demo, 'price': 10000000,
             'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'brand_id': brands.get('xiaomi'), 'category_id': categories.get('phone'), 'name': 'Redmi K30',
             'code': 'redmi-k30', 'type': 0, 'description': self.description_demo, 'price': 10000000,
             'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'brand_id': brands.get('xiaomi'), 'category_id': categories.get('phone'), 'name': 'Redmi K20 Pro',
             'code': 'redmi-k20-pro', 'type': 0, 'description': self.description_demo, 'price': 10000000,
             'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'brand_id': brands.get('xiaomi'), 'category_id': categories.get('phone'), 'name': 'Redmi K20',
             'code': 'redmi-k20', 'type': 0, 'description': self.description_demo, 'price': 10000000,
             'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
        ]
        apple_headphone = [
            {'brand_id': brands.get('apple'), 'category_id': categories.get('headphone'), 'name': 'AirPods 1',
             'code': 'air-pods-1', 'type': 0, 'description': self.description_demo, 'price': 2000000,
             'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'brand_id': brands.get('apple'), 'category_id': categories.get('headphone'), 'name': 'AirPods 2',
             'code': 'air-pods-2', 'type': 0, 'description': self.description_demo, 'price': 4000000,
             'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'brand_id': brands.get('apple'), 'category_id': categories.get('headphone'), 'name': 'AirPods Pro',
             'code': 'air-pods-pro', 'type': 0, 'description': self.description_demo, 'price': 6000000,
             'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'brand_id': brands.get('apple'), 'category_id': categories.get('headphone'), 'name': 'AirPods Max',
             'code': 'air-pods-max', 'type': 0, 'description': self.description_demo, 'price': 6000000,
             'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'brand_id': brands.get('apple'), 'category_id': categories.get('headphone'), 'name': 'Beats Solo Pro',
             'code': 'beats-solo-pro', 'type': 0, 'description': self.description_demo, 'price': 6000000,
             'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'brand_id': brands.get('apple'), 'category_id': categories.get('headphone'), 'name': 'Beats Studio 3',
             'code': 'beats-studio-3', 'type': 0, 'description': self.description_demo, 'price': 6000000,
             'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
        ]
        apple_pc = [
            {'brand_id': brands.get('apple'), 'category_id': categories.get('pc'), 'name': 'Macbook Air',
             'code': 'macbook-air', 'type': 0, 'description': self.description_demo, 'price': 2000000,
             'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'brand_id': brands.get('apple'), 'category_id': categories.get('pc'), 'name': 'Macbook Pro',
             'code': 'macbook-pro', 'type': 0, 'description': self.description_demo, 'price': 4000000,
             'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'brand_id': brands.get('apple'), 'category_id': categories.get('pc'), 'name': 'iMac',
             'code': 'imac', 'type': 0, 'description': self.description_demo, 'price': 6000000,
             'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
        ]
        apple_watch = [
            {'brand_id': brands.get('apple'), 'category_id': categories.get('watch'), 'name': 'Apple Watch Series 1',
             'code': 'aw-s1', 'type': 0, 'description': self.description_demo, 'price': 20000000,
             'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'brand_id': brands.get('apple'), 'category_id': categories.get('watch'), 'name': 'Apple Watch Series 2',
             'code': 'aw-s2', 'type': 0, 'description': self.description_demo, 'price': 20000000,
             'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'brand_id': brands.get('apple'), 'category_id': categories.get('watch'), 'name': 'Apple Watch Series 3',
             'code': 'aw-s3', 'type': 0, 'description': self.description_demo, 'price': 20000000,
             'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'brand_id': brands.get('apple'), 'category_id': categories.get('watch'), 'name': 'Apple Watch Series 4',
             'code': 'aw-s4', 'type': 0, 'description': self.description_demo, 'price': 20000000,
             'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'brand_id': brands.get('apple'), 'category_id': categories.get('watch'), 'name': 'Apple Watch Series 5',
             'code': 'aw-s5', 'type': 0, 'description': self.description_demo, 'price': 20000000,
             'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
            {'brand_id': brands.get('apple'), 'category_id': categories.get('watch'), 'name': 'Apple Watch Series 6',
             'code': 'aw-s6', 'type': 0, 'description': self.description_demo, 'price': 20000000,
             'status': 1, 'create_date': str(self.now), 'create_by': 3,
             'update_date': str(self.now), 'update_by': 3},
        ]
        parent = apple_phone + xiaomi_phone + apple_headphone + apple_pc + apple_watch
        self.execute('product', parent)

    def product_variant_import(self):
        brands, categories, product_options, product_option_names = self.get_map(True)
        self.cur.execute('select product_id, brand_id, category_id, name, code from product where type = 0')
        parent_data = self.cur.fetchall()
        for p in parent_data:
            apple_variant_phone = list()
            apple_variant_headphone = list()
            apple_variant_pc = list()
            apple_variant_watch = list()
            xiaomi_variant_phone = list()
            p_data = {'product_id': p[0], 'brand_id': p[1], 'category_id': p[2], 'name': p[3], 'code': p[4]}
            if p_data['brand_id'] == brands.get('apple'):
                if p_data['category_id'] == categories.get('phone'):
                    v_ram3gb_memory64gb = [
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-3gb') + ' - '
                                 + product_option_names.get('memory-64gb') + ' - '
                                 + product_option_names.get('black'),
                         'code': p_data['code'] + '_' + 'ram-3gb' + '_' + 'memory-64gb' + '_' + 'black',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-3gb') + ' - '
                                 + product_option_names.get('memory-64gb') + ' - '
                                 + product_option_names.get('blue'),
                         'code': p_data['code'] + '_' + 'ram-3gb' + '_' + 'memory-64gb' + '_' + 'blue',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-3gb') + ' - '
                                 + product_option_names.get('memory-64gb') + ' - '
                                 + product_option_names.get('gold'),
                         'code': p_data['code'] + '_' + 'ram-3gb' + '_' + 'memory-64gb' + '_' + 'gold',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-3gb') + ' - '
                                 + product_option_names.get('memory-64gb') + ' - '
                                 + product_option_names.get('midnight-green'),
                         'code': p_data['code'] + '_' + 'ram-3gb' + '_' + 'memory-64gb' + '_' + 'midnight-green',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                    ]
                    v_ram3gb_memory128gb = [
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-3gb') + ' - '
                                 + product_option_names.get('memory-128gb') + ' - '
                                 + product_option_names.get('black'),
                         'code': p_data['code'] + '_' + 'ram-3gb' + '_' + 'memory-128gb' + '_' + 'black',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-3gb') + ' - '
                                 + product_option_names.get('memory-128gb') + ' - '
                                 + product_option_names.get('blue'),
                         'code': p_data['code'] + '_' + 'ram-3gb' + '_' + 'memory-128gb' + '_' + 'blue',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-3gb') + ' - '
                                 + product_option_names.get('memory-128gb') + ' - '
                                 + product_option_names.get('gold'),
                         'code': p_data['code'] + '_' + 'ram-3gb' + '_' + 'memory-128gb' + '_' + 'gold',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-3gb') + ' - '
                                 + product_option_names.get('memory-128gb') + ' - '
                                 + product_option_names.get('midnight-green'),
                         'code': p_data['code'] + '_' + 'ram-3gb' + '_' + 'memory-128gb' + '_' + 'midnight-green',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                    ]
                    v_ram3gb_memory256gb = [
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-3gb') + ' - '
                                 + product_option_names.get('memory-256gb') + ' - '
                                 + product_option_names.get('black'),
                         'code': p_data['code'] + '_' + 'ram-3gb' + '_' + 'memory-256gb' + '_' + 'black',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-3gb') + ' - '
                                 + product_option_names.get('memory-256gb') + ' - '
                                 + product_option_names.get('blue'),
                         'code': p_data['code'] + '_' + 'ram-3gb' + '_' + 'memory-256gb' + '_' + 'blue',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-3gb') + ' - '
                                 + product_option_names.get('memory-256gb') + ' - '
                                 + product_option_names.get('gold'),
                         'code': p_data['code'] + '_' + 'ram-3gb' + '_' + 'memory-256gb' + '_' + 'gold',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-3gb') + ' - '
                                 + product_option_names.get('memory-256gb') + ' - '
                                 + product_option_names.get('midnight-green'),
                         'code': p_data['code'] + '_' + 'ram-3gb' + '_' + 'memory-256gb' + '_' + 'midnight-green',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                    ]
                    v_ram3gb = v_ram3gb_memory64gb + v_ram3gb_memory128gb + v_ram3gb_memory256gb

                    v_ram4gb_memory64gb = [
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-4gb') + ' - '
                                 + product_option_names.get('memory-64gb') + ' - '
                                 + product_option_names.get('black'),
                         'code': p_data['code'] + '_' + 'ram-4gb' + '_' + 'memory-64gb' + '_' + 'black',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-4gb') + ' - '
                                 + product_option_names.get('memory-64gb') + ' - '
                                 + product_option_names.get('blue'),
                         'code': p_data['code'] + '_' + 'ram-4gb' + '_' + 'memory-64gb' + '_' + 'blue',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-4gb') + ' - '
                                 + product_option_names.get('memory-64gb') + ' - '
                                 + product_option_names.get('gold'),
                         'code': p_data['code'] + '_' + 'ram-4gb' + '_' + 'memory-64gb' + '_' + 'gold',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-4gb') + ' - '
                                 + product_option_names.get('memory-64gb') + ' - '
                                 + product_option_names.get('midnight-green'),
                         'code': p_data['code'] + '_' + 'ram-4gb' + '_' + 'memory-64gb' + '_' + 'midnight-green',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                    ]
                    v_ram4gb_memory128gb = [
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-4gb') + ' - '
                                 + product_option_names.get('memory-128gb') + ' - '
                                 + product_option_names.get('black'),
                         'code': p_data['code'] + '_' + 'ram-4gb' + '_' + 'memory-128gb' + '_' + 'black',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-4gb') + ' - '
                                 + product_option_names.get('memory-128gb') + ' - '
                                 + product_option_names.get('blue'),
                         'code': p_data['code'] + '_' + 'ram-4gb' + '_' + 'memory-128gb' + '_' + 'blue',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-4gb') + ' - '
                                 + product_option_names.get('memory-128gb') + ' - '
                                 + product_option_names.get('gold'),
                         'code': p_data['code'] + '_' + 'ram-4gb' + '_' + 'memory-128gb' + '_' + 'gold',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-4gb') + ' - '
                                 + product_option_names.get('memory-128gb') + ' - '
                                 + product_option_names.get('midnight-green'),
                         'code': p_data['code'] + '_' + 'ram-4gb' + '_' + 'memory-128gb' + '_' + 'midnight-green',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                    ]
                    v_ram4gb_memory256gb = [
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-4gb') + ' - '
                                 + product_option_names.get('memory-256gb') + ' - '
                                 + product_option_names.get('black'),
                         'code': p_data['code'] + '_' + 'ram-4gb' + '_' + 'memory-256gb' + '_' + 'black',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-4gb') + ' - '
                                 + product_option_names.get('memory-256gb') + ' - '
                                 + product_option_names.get('blue'),
                         'code': p_data['code'] + '_' + 'ram-4gb' + '_' + 'memory-256gb' + '_' + 'blue',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-4gb') + ' - '
                                 + product_option_names.get('memory-256gb') + ' - '
                                 + product_option_names.get('gold'),
                         'code': p_data['code'] + '_' + 'ram-4gb' + '_' + 'memory-256gb' + '_' + 'gold',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-4gb') + ' - '
                                 + product_option_names.get('memory-256gb') + ' - '
                                 + product_option_names.get('midnight-green'),
                         'code': p_data['code'] + '_' + 'ram-4gb' + '_' + 'memory-256gb' + '_' + 'midnight-green',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                    ]
                    v_ram4gb = v_ram4gb_memory64gb + v_ram4gb_memory128gb + v_ram4gb_memory256gb

                    apple_variant_phone = v_ram3gb + v_ram4gb
                elif p_data['category_id'] == categories.get('headphone'):
                    v_df_charge = [
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('default-charge'),
                         'code': p_data['code'] + '_' + 'default-charge',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                    ]
                    v_air_charge = [
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('air-charge'),
                         'code': p_data['code'] + '_' + 'air-charge',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                    ]
                    apple_variant_headphone = v_df_charge + v_air_charge
                elif p_data['category_id'] == categories.get('pc'):
                    v_ram16gb_memory256gb_2018_touch = [
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-16gb') + ' - '
                                 + product_option_names.get('memory-256gb') + ' - '
                                 + product_option_names.get('2018') + ' - '
                                 + product_option_names.get('has-touch'),
                         'code': p_data['code'] + '_' + 'ram-16gb' + '_' + 'memory-256gb' + '_' + '2018' + '_' + 'has-touch',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                    ]
                    v_ram16gb_memory256gb_2018_notouch = [
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-16gb') + ' - '
                                 + product_option_names.get('memory-256gb') + ' - '
                                 + product_option_names.get('2018') + ' - '
                                 + product_option_names.get('non-touch'),
                         'code': p_data['code'] + '_' + 'ram-16gb' + '_' + 'memory-256gb' + '_' + '2018' + '_' + 'non-touch',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                    ]
                    v_ram16gb_memory256gb_2018 = v_ram16gb_memory256gb_2018_touch + v_ram16gb_memory256gb_2018_notouch

                    v_ram16gb_memory256gb_2020_touch = [
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-16gb') + ' - '
                                 + product_option_names.get('memory-256gb') + ' - '
                                 + product_option_names.get('2020') + ' - '
                                 + product_option_names.get('has-touch'),
                         'code': p_data['code'] + '_' + 'ram-16gb' + '_' + 'memory-256gb' + '_' + '2020' + '_' + 'has-touch',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                    ]
                    v_ram16gb_memory256gb_2020_notouch = [
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-16gb') + ' - '
                                 + product_option_names.get('memory-256gb') + ' - '
                                 + product_option_names.get('2020') + ' - '
                                 + product_option_names.get('non-touch'),
                         'code': p_data['code'] + '_' + 'ram-16gb' + '_' + 'memory-256gb' + '_' + '2020' + '_' + 'non-touch',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                    ]
                    v_ram16gb_memory256gb_2020 = v_ram16gb_memory256gb_2020_touch + v_ram16gb_memory256gb_2020_notouch

                    v_ram16gb_memory256gb = v_ram16gb_memory256gb_2018 + v_ram16gb_memory256gb_2020

                    v_ram16gb_memory512gb_2018_touch = [
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-16gb') + ' - '
                                 + product_option_names.get('memory-512gb') + ' - '
                                 + product_option_names.get('2018') + ' - '
                                 + product_option_names.get('has-touch'),
                         'code': p_data['code'] + '_' + 'ram-16gb' + '_' + 'memory-512gb' + '_' + '2018' + '_' + 'has-touch',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                    ]
                    v_ram16gb_memory512gb_2018_notouch = [
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-16gb') + ' - '
                                 + product_option_names.get('memory-512gb') + ' - '
                                 + product_option_names.get('2018') + ' - '
                                 + product_option_names.get('non-touch'),
                         'code': p_data['code'] + '_' + 'ram-16gb' + '_' + 'memory-512gb' + '_' + '2018' + '_' + 'non-touch',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                    ]
                    v_ram16gb_memory512gb_2018 = v_ram16gb_memory512gb_2018_touch + v_ram16gb_memory512gb_2018_notouch

                    v_ram16gb_memory512gb_2020_touch = [
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-16gb') + ' - '
                                 + product_option_names.get('memory-512gb') + ' - '
                                 + product_option_names.get('2020') + ' - '
                                 + product_option_names.get('has-touch'),
                         'code': p_data['code'] + '_' + 'ram-16gb' + '_' + 'memory-512gb' + '_' + '2018' + '_' + 'has-touch',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                    ]
                    v_ram16gb_memory512gb_2020_notouch = [
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-16gb') + ' - '
                                 + product_option_names.get('memory-512gb') + ' - '
                                 + product_option_names.get('2020') + ' - '
                                 + product_option_names.get('non-touch'),
                         'code': p_data['code'] + '_' + 'ram-16gb' + '_' + 'memory-512gb' + '_' + '2020' + '_' + 'non-touch',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                    ]
                    v_ram16gb_memory512gb_2020 = v_ram16gb_memory512gb_2020_touch + v_ram16gb_memory512gb_2020_notouch

                    v_ram16gb_memory512gb = v_ram16gb_memory512gb_2018 + v_ram16gb_memory512gb_2020

                    v_ram16gb = v_ram16gb_memory256gb + v_ram16gb_memory512gb

                    v_ram32gb_memory256gb_2018_touch = [
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-32gb') + ' - '
                                 + product_option_names.get('memory-256gb') + ' - '
                                 + product_option_names.get('2018') + ' - '
                                 + product_option_names.get('has-touch'),
                         'code': p_data['code'] + '_' + 'ram-32gb' + '_' + 'memory-256gb' + '_' + '2018' + '_' + 'has-touch',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                    ]
                    v_ram32gb_memory256gb_2018_notouch = [
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-32gb') + ' - '
                                 + product_option_names.get('memory-256gb') + ' - '
                                 + product_option_names.get('2018') + ' - '
                                 + product_option_names.get('non-touch'),
                         'code': p_data['code'] + '_' + 'ram-32gb' + '_' + 'memory-256gb' + '_' + '2018' + '_' + 'non-touch',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                    ]
                    v_ram32gb_memory256gb_2018 = v_ram32gb_memory256gb_2018_touch + v_ram32gb_memory256gb_2018_notouch

                    v_ram32gb_memory256gb_2020_touch = [
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-32gb') + ' - '
                                 + product_option_names.get('memory-256gb') + ' - '
                                 + product_option_names.get('2020') + ' - '
                                 + product_option_names.get('has-touch'),
                         'code': p_data['code'] + '_' + 'ram-32gb' + '_' + 'memory-256gb' + '_' + '2020' + '_' + 'has-touch',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                    ]
                    v_ram32gb_memory256gb_2020_notouch = [
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-32gb') + ' - '
                                 + product_option_names.get('memory-256gb') + ' - '
                                 + product_option_names.get('2020') + ' - '
                                 + product_option_names.get('non-touch'),
                         'code': p_data['code'] + '_' + 'ram-32gb' + '_' + 'memory-256gb' + '_' + '2020' + '_' + 'non-touch',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                    ]
                    v_ram32gb_memory256gb_2020 = v_ram32gb_memory256gb_2020_touch + v_ram32gb_memory256gb_2020_notouch

                    v_ram32gb_memory256gb = v_ram32gb_memory256gb_2018 + v_ram32gb_memory256gb_2020

                    v_ram32gb_memory512gb_2018_touch = [
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-32gb') + ' - '
                                 + product_option_names.get('memory-512gb') + ' - '
                                 + product_option_names.get('2018') + ' - '
                                 + product_option_names.get('has-touch'),
                         'code': p_data['code'] + '_' + 'ram-32gb' + '_' + 'memory-512gb' + '_' + '2018' + '_' + 'has-touch',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                    ]
                    v_ram32gb_memory512gb_2018_notouch = [
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-32gb') + ' - '
                                 + product_option_names.get('memory-512gb') + ' - '
                                 + product_option_names.get('2018') + ' - '
                                 + product_option_names.get('non-touch'),
                         'code': p_data['code'] + '_' + 'ram-32gb' + '_' + 'memory-512gb' + '_' + '2018' + '_' + 'non-touch',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                    ]
                    v_ram32gb_memory512gb_2018 = v_ram32gb_memory512gb_2018_touch + v_ram32gb_memory512gb_2018_notouch

                    v_ram32gb_memory512gb_2020_touch = [
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-32gb') + ' - '
                                 + product_option_names.get('memory-512gb') + ' - '
                                 + product_option_names.get('2020') + ' - '
                                 + product_option_names.get('has-touch'),
                         'code': p_data[
                                     'code'] + '_' + 'ram-32gb' + '_' + 'memory-512gb' + '_' + '2018' + '_' + 'has-touch',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                    ]
                    v_ram32gb_memory512gb_2020_notouch = [
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('ram-32gb') + ' - '
                                 + product_option_names.get('memory-512gb') + ' - '
                                 + product_option_names.get('2020') + ' - '
                                 + product_option_names.get('non-touch'),
                         'code': p_data['code'] + '_' + 'ram-32gb' + '_' + 'memory-512gb' + '_' + '2020' + '_' + 'non-touch',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                    ]
                    v_ram32gb_memory512gb_2020 = v_ram32gb_memory512gb_2020_touch + v_ram32gb_memory512gb_2020_notouch

                    v_ram32gb_memory512gb = v_ram32gb_memory512gb_2018 + v_ram32gb_memory512gb_2020

                    v_ram32gb = v_ram32gb_memory256gb + v_ram32gb_memory512gb

                    apple_variant_pc = v_ram16gb + v_ram32gb
                else:
                    v_38mm = [
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('38mm') + ' - '
                                 + product_option_names.get('metal'),
                         'code': p_data['code'] + '_' + '38mm' + '_' + 'metal',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('38mm') + ' - '
                                 + product_option_names.get('non-metal'),
                         'code': p_data['code'] + '_' + '38mm' + '_' + 'non-metal',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                    ]
                    v_40mm = [
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('40mm') + ' - '
                                 + product_option_names.get('metal'),
                         'code': p_data['code'] + '_' + '40mm' + '_' + 'metal',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('40mm') + ' - '
                                 + product_option_names.get('non-metal'),
                         'code': p_data['code'] + '_' + '40mm' + '_' + 'non-metal',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                    ]
                    v_42mm = [
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('42mm') + ' - '
                                 + product_option_names.get('metal'),
                         'code': p_data['code'] + '_' + '42mm' + '_' + 'metal',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('42mm') + ' - '
                                 + product_option_names.get('non-metal'),
                         'code': p_data['code'] + '_' + '42mm' + '_' + 'non-metal',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                    ]
                    v_44mm = [
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('44mm') + ' - '
                                 + product_option_names.get('metal'),
                         'code': p_data['code'] + '_' + '44mm' + '_' + 'metal',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                        {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                         'name': p_data['name'] + ' - ' + product_option_names.get('44mm') + ' - '
                                 + product_option_names.get('non-metal'),
                         'code': p_data['code'] + '_' + '44mm' + '_' + 'non-metal',
                         'quantity': 10, 'type': 1, 'description': self.description_demo,
                         'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                         'update_date': str(self.now), 'update_by': 3},
                    ]
                    apple_variant_watch = v_38mm + v_40mm + v_42mm + v_44mm
            else:
                v_ram6gb_memory128gb = [
                    {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                     'name': p_data['name'] + ' - ' + product_option_names.get('ram-6gb') + ' - '
                             + product_option_names.get('memory-128gb') + ' - '
                             + product_option_names.get('black'),
                     'code': p_data['code'] + '_' + 'ram-6gb' + '_' + 'memory-128gb' + '_' + 'black',
                     'quantity': 10, 'type': 1, 'description': self.description_demo,
                     'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                     'update_date': str(self.now), 'update_by': 3},
                    {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                     'name': p_data['name'] + ' - ' + product_option_names.get('ram-6gb') + ' - '
                             + product_option_names.get('memory-128gb') + ' - '
                             + product_option_names.get('blue'),
                     'code': p_data['code'] + '_' + 'ram-6gb' + '_' + 'memory-128gb' + '_' + 'blue',
                     'quantity': 10, 'type': 1, 'description': self.description_demo,
                     'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                     'update_date': str(self.now), 'update_by': 3},
                    {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                     'name': p_data['name'] + ' - ' + product_option_names.get('ram-6gb') + ' - '
                             + product_option_names.get('memory-128gb') + ' - '
                             + product_option_names.get('white'),
                     'code': p_data['code'] + '_' + 'ram-6gb' + '_' + 'memory-128gb' + '_' + 'white',
                     'quantity': 10, 'type': 1, 'description': self.description_demo,
                     'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                     'update_date': str(self.now), 'update_by': 3},
                    {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                     'name': p_data['name'] + ' - ' + product_option_names.get('ram-6gb') + ' - '
                             + product_option_names.get('memory-128gb') + ' - '
                             + product_option_names.get('red'),
                     'code': p_data['code'] + '_' + 'ram-6gb' + '_' + 'memory-128gb' + '_' + 'red',
                     'quantity': 10, 'type': 1, 'description': self.description_demo,
                     'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                     'update_date': str(self.now), 'update_by': 3},
                    {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                     'name': p_data['name'] + ' - ' + product_option_names.get('ram-6gb') + ' - '
                             + product_option_names.get('memory-128gb') + ' - '
                             + product_option_names.get('purple'),
                     'code': p_data['code'] + '_' + 'ram-6gb' + '_' + 'memory-128gb' + '_' + 'purple',
                     'quantity': 10, 'type': 1, 'description': self.description_demo,
                     'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                     'update_date': str(self.now), 'update_by': 3},
                ]
                v_ram6gb_memory256gb = [
                    {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                     'name': p_data['name'] + ' - ' + product_option_names.get('ram-6gb') + ' - '
                             + product_option_names.get('memory-256gb') + ' - '
                             + product_option_names.get('black'),
                     'code': p_data['code'] + '_' + 'ram-6gb' + '_' + 'memory-256gb' + '_' + 'black',
                     'quantity': 10, 'type': 1, 'description': self.description_demo,
                     'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                     'update_date': str(self.now), 'update_by': 3},
                    {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                     'name': p_data['name'] + ' - ' + product_option_names.get('ram-6gb') + ' - '
                             + product_option_names.get('memory-256gb') + ' - '
                             + product_option_names.get('blue'),
                     'code': p_data['code'] + '_' + 'ram-6gb' + '_' + 'memory-256gb' + '_' + 'blue',
                     'quantity': 10, 'type': 1, 'description': self.description_demo,
                     'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                     'update_date': str(self.now), 'update_by': 3},
                    {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                     'name': p_data['name'] + ' - ' + product_option_names.get('ram-6gb') + ' - '
                             + product_option_names.get('memory-256gb') + ' - '
                             + product_option_names.get('white'),
                     'code': p_data['code'] + '_' + 'ram-6gb' + '_' + 'memory-256gb' + '_' + 'white',
                     'quantity': 10, 'type': 1, 'description': self.description_demo,
                     'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                     'update_date': str(self.now), 'update_by': 3},
                    {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                     'name': p_data['name'] + ' - ' + product_option_names.get('ram-6gb') + ' - '
                             + product_option_names.get('memory-256gb') + ' - '
                             + product_option_names.get('red'),
                     'code': p_data['code'] + '_' + 'ram-6gb' + '_' + 'memory-256gb' + '_' + 'red',
                     'quantity': 10, 'type': 1, 'description': self.description_demo,
                     'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                     'update_date': str(self.now), 'update_by': 3},
                    {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                     'name': p_data['name'] + ' - ' + product_option_names.get('ram-6gb') + ' - '
                             + product_option_names.get('memory-256gb') + ' - '
                             + product_option_names.get('purple'),
                     'code': p_data['code'] + '_' + 'ram-6gb' + '_' + 'memory-256gb' + '_' + 'purple',
                     'quantity': 10, 'type': 1, 'description': self.description_demo,
                     'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                     'update_date': str(self.now), 'update_by': 3},
                ]
                v_ram6gb = v_ram6gb_memory128gb + v_ram6gb_memory256gb

                v_ram8gb_memory128gb = [
                    {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                     'name': p_data['name'] + ' - ' + product_option_names.get('ram-8gb') + ' - '
                             + product_option_names.get('memory-128gb') + ' - '
                             + product_option_names.get('black'),
                     'code': p_data['code'] + '_' + 'ram-8gb' + '_' + 'memory-128gb' + '_' + 'black',
                     'quantity': 10, 'type': 1, 'description': self.description_demo,
                     'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                     'update_date': str(self.now), 'update_by': 3},
                    {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                     'name': p_data['name'] + ' - ' + product_option_names.get('ram-8gb') + ' - '
                             + product_option_names.get('memory-128gb') + ' - '
                             + product_option_names.get('blue'),
                     'code': p_data['code'] + '_' + 'ram-8gb' + '_' + 'memory-128gb' + '_' + 'blue',
                     'quantity': 10, 'type': 1, 'description': self.description_demo,
                     'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                     'update_date': str(self.now), 'update_by': 3},
                    {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                     'name': p_data['name'] + ' - ' + product_option_names.get('ram-8gb') + ' - '
                             + product_option_names.get('memory-128gb') + ' - '
                             + product_option_names.get('white'),
                     'code': p_data['code'] + '_' + 'ram-8gb' + '_' + 'memory-128gb' + '_' + 'white',
                     'quantity': 10, 'type': 1, 'description': self.description_demo,
                     'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                     'update_date': str(self.now), 'update_by': 3},
                    {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                     'name': p_data['name'] + ' - ' + product_option_names.get('ram-8gb') + ' - '
                             + product_option_names.get('memory-128gb') + ' - '
                             + product_option_names.get('red'),
                     'code': p_data['code'] + '_' + 'ram-8gb' + '_' + 'memory-128gb' + '_' + 'red',
                     'quantity': 10, 'type': 1, 'description': self.description_demo,
                     'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                     'update_date': str(self.now), 'update_by': 3},
                    {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                     'name': p_data['name'] + ' - ' + product_option_names.get('ram-8gb') + ' - '
                             + product_option_names.get('memory-128gb') + ' - '
                             + product_option_names.get('purple'),
                     'code': p_data['code'] + '_' + 'ram-8gb' + '_' + 'memory-128gb' + '_' + 'purple',
                     'quantity': 10, 'type': 1, 'description': self.description_demo,
                     'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                     'update_date': str(self.now), 'update_by': 3},
                ]
                v_ram8gb_memory256gb = [
                    {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                     'name': p_data['name'] + ' - ' + product_option_names.get('ram-8gb') + ' - '
                             + product_option_names.get('memory-256gb') + ' - '
                             + product_option_names.get('black'),
                     'code': p_data['code'] + '_' + 'ram-8gb' + '_' + 'memory-256gb' + '_' + 'black',
                     'quantity': 10, 'type': 1, 'description': self.description_demo,
                     'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                     'update_date': str(self.now), 'update_by': 3},
                    {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                     'name': p_data['name'] + ' - ' + product_option_names.get('ram-8gb') + ' - '
                             + product_option_names.get('memory-256gb') + ' - '
                             + product_option_names.get('blue'),
                     'code': p_data['code'] + '_' + 'ram-8gb' + '_' + 'memory-256gb' + '_' + 'blue',
                     'quantity': 10, 'type': 1, 'description': self.description_demo,
                     'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                     'update_date': str(self.now), 'update_by': 3},
                    {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                     'name': p_data['name'] + ' - ' + product_option_names.get('ram-8gb') + ' - '
                             + product_option_names.get('memory-256gb') + ' - '
                             + product_option_names.get('white'),
                     'code': p_data['code'] + '_' + 'ram-8gb' + '_' + 'memory-256gb' + '_' + 'white',
                     'quantity': 10, 'type': 1, 'description': self.description_demo,
                     'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                     'update_date': str(self.now), 'update_by': 3},
                    {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                     'name': p_data['name'] + ' - ' + product_option_names.get('ram-8gb') + ' - '
                             + product_option_names.get('memory-256gb') + ' - '
                             + product_option_names.get('red'),
                     'code': p_data['code'] + '_' + 'ram-8gb' + '_' + 'memory-256gb' + '_' + 'red',
                     'quantity': 10, 'type': 1, 'description': self.description_demo,
                     'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                     'update_date': str(self.now), 'update_by': 3},
                    {'brand_id': p_data['brand_id'], 'category_id': p_data['category_id'],
                     'name': p_data['name'] + ' - ' + product_option_names.get('ram-8gb') + ' - '
                             + product_option_names.get('memory-256gb') + ' - '
                             + product_option_names.get('purple'),
                     'code': p_data['code'] + '_' + 'ram-8gb' + '_' + 'memory-256gb' + '_' + 'purple',
                     'quantity': 10, 'type': 1, 'description': self.description_demo,
                     'price': 20000000, 'status': 1, 'create_date': str(self.now), 'create_by': 3,
                     'update_date': str(self.now), 'update_by': 3},
                ]
                v_ram8gb = v_ram8gb_memory128gb + v_ram8gb_memory256gb

                xiaomi_variant_phone = v_ram6gb + v_ram8gb

            variants = apple_variant_phone + apple_variant_headphone + apple_variant_pc + apple_variant_watch + xiaomi_variant_phone
            self.execute('product', variants, False)

    def product_variant_expert(self):
        brands, categories, product_options, product_option_names = self.get_map(True)

        # get map parent
        self.cur.execute('select product_id, code from product where type = 0')
        parent_data = self.cur.fetchall()
        parent_map = {code: product_id for product_id, code in parent_data}

        # get list variant
        self.cur.execute('select product_id, code from product where type = 1')
        variant_data = self.cur.fetchall()

        for v in variant_data:
            v_data = {'product_id': v[0], 'code': v[1]}
            data_repair = str(v_data['code']).split('_')
            print(data_repair)


if __name__ == '__main__':
    lazy_import().main()
