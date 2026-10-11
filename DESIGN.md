# Quy tắc thiết kế

Tài liệu này mô tả cách mỗi component trong kit phải trông và hoạt động, để component mới và các chỉnh sửa sau này nhất quán. Giá trị cụ thể lấy từ code; khi code đổi thì sửa tài liệu này theo.

## 1. Nguyên tắc

1. **Phẳng.** Không đổ bóng, không gradient, không bo góc lớn. Bo góc chỉ 2dp (`cornerRadius`) ở nút; nút tròn là ngoại lệ duy nhất.
2. **Một accent.** Cả app dùng một màu accent. Accent chỉ dành cho: trạng thái đang chọn / bật, nút xác nhận, đường nhấn dưới title bar, nền highlight khi focus (accent tối hơn một chút), chữ action ở title bar.
3. **Bàn phím trước, cảm ứng vẫn tốt.** Mọi thứ chạm được đều focus được và kích hoạt được bằng Enter / Space. Không có thao tác chỉ làm được bằng cảm ứng.
4. **Xác nhận ở trên, hành động chính ở dưới.** Cancel/OK nằm ở title bar; một hành động chính của màn hình là nút tròn to ở bottom bar.
5. **Không phụ thuộc Material.** Chỉ dùng `foundation` và `ui`.
6. **Không dùng tài sản của BlackBerry.** Icon vẽ bằng code, font là font hệ thống.
7. **Mọi giá trị từ token.** Không viết màu, khoảng cách, cỡ chữ trực tiếp trong component; lấy từ `Bb10.colors`, `Bb10.dimens`, `Bb10.typography`.

## 2. Token

### Màu

| Token | Dark | Light |
|---|---|---|
| `background` | `#000000` | `#F8F8F8` |
| `surface` | `#1F1F1F` | `#FFFFFF` |
| `surfaceVariant` | `#262626` | `#EDEDED` |
| `divider` | `#3A3A3A` | `#D6D6D6` |
| `field` (nền input) | `#262626` | `#EEEEEE` |
| `textPrimary` | `#FFFFFF` | `#262626` |
| `textSecondary` | `#A6A6A6` | `#6E6E6E` |
| `textDisabled` | `#666666` | `#B0B0B0` |
| `error` | `#FF5A5F` | `#D9363E` |
| `accentTop` → `accentBottom` | `#0080B2` → `#008FC9` | giống dark |
| `selectionTop` → `selectionBottom` | `#087098` → `#065F81` | giống dark |
| `focusFill` | `#087099` (phẳng) | giống dark |
| `pressedFill` | `#065F81` (phẳng) | giống dark |
| `scrim` | đen 60% | đen 40% |

**Phần tử tô màu accent không phẳng mà là gradient dọc** (trên → dưới): nút công tắc bật, ô checkbox đã chọn, nút primary / nút `primary`, thanh dropdown đang mở, nút xác nhận một nút của dialog, ô cỡ đang chọn, vòng tròn primary: `accentTop` → `accentBottom`. Thanh được highlight (thanh dropdown, nút, tab, ô trong bottom bar) dùng gradient `selectionTop` → `selectionBottom`. **Dòng** được highlight hoặc đang chọn (list item, mục menu, lựa chọn trong dropdown, mục menu điều hướng) dùng màu phẳng `focusFill`, chữ + icon trắng; lựa chọn đang chọn của dropdown cũng như vậy. Các giá trị trên là mẫu màu của BB10 cho accent mặc định; accent khác được suy ra bằng cách làm tối. Đường kẻ và chữ accent vẫn là màu phẳng `accent`.

`accent` mặc định `#0092CC` (Blue); có sẵn Teal, Green, Orange, Red, Purple trong `Bb10Accents`. `onAccent` luôn là trắng.

Quy tắc dùng màu:
- Chữ chính trên nền: `textPrimary`; phụ đề, nhãn action, caption: `textSecondary`; placeholder, disabled: `textDisabled`.
- Mặt phẳng: nền màn hình `background`; thanh (title, action) và dialog `surfaceVariant`; panel trượt `surface`.
- Chữ trên nền accent luôn `onAccent`.
- Disabled không đổi màu riêng mà giảm alpha còn 0.4 (làm trong `bb10Clickable`).

### Kích thước

| Token | Regular | Compact (màn gần vuông) |
|---|---|---|
| `spaceXs / S / M / L` | 4 / 8 / 12 / 16 | 4 / 6 / 10 / 12 |
| `titleBarHeight` | 56 | 48 |
| `actionBarHeight` | 64 | 52 |
| `primaryActionSize` | 56 | 48 |
| `listItemMinHeight` | 56 | 48 |
| `controlHeight` | 44 | 40 |
| `minTouchTarget` | 48 | 48 |
| `iconSize` | 24 | 22 |
| `cornerRadius` / `dividerThickness` / `focusBorder` | 2 / 1 / 2 | 2 / 1 / 2 |

Compact được chọn khi chênh lệch chiều rộng và chiều cao của màn hình không quá 40dp (`rememberIsSquare`).

### Chữ

| Style | Cỡ | Đậm | Ghi chú |
|---|---|---|---|
| `title` | 18 | Light | tiêu đề, tiêu đề dialog |
| `header` | 11 | Medium | chữ HOA, giãn chữ 0.8 |
| `body` | 15 | Normal | nội dung, tiêu đề dòng list |
| `secondary` | 13 | Normal | phụ đề, label form |
| `caption` | 11 | Normal | giờ, số đếm, gợi ý phím |
| `button` | 14 | Medium | nút, action ở title bar |
| `action` | 10 | Normal | nhãn dưới icon ở bottom bar |

### Co giãn (scaling, bắt buộc)

- **Mọi app dùng kit phải có cài đặt cỡ hiển thị.** `Bb10Theme(scale = ...)` bắt buộc truyền `Bb10ScaleState` (từ `rememberBb10UiScale()`), và màn Settings của app phải có `Bb10ScalePicker` (hoặc điều khiển tương đương dùng cùng state) để người dùng lớn tuổi tự chỉnh.
- Kích thước trong token và trong component là dp/sp **ở cỡ thiết kế (scale = 1)**. Không tự nhân hệ số trong component; `Bb10Theme` scale `Density`, nên mọi dp/sp đổi theo cùng một hệ số.
- Vì vậy chỉ dùng dp/sp (không dùng px, không đọc kích thước màn hình rồi tự tính) trong component.
- Các bước: Small 0.85, Normal 1, Large 1.25, Extra large 1.5, Huge 2. Cỡ chữ, icon, vùng chạm, thanh, panel và dialog to lên cùng nhau.
- Compact / Regular chọn theo tỉ lệ màn hình thật (gần vuông hay không), không đổi theo `scale`.
- Khi thêm component: kiểm tra ở `scale = 2` rằng không bị cắt, chữ không bị đè lên nhau và vẫn cuộn được.

### Font

- Chỉ dùng font hệ thống (`FontFamily.Default`); kit không có tùy chọn font riêng. App theo font và cỡ chữ mà người dùng đặt trong cài đặt thiết bị.
- Phân cấp chữ làm bằng cỡ và độ đậm (bảng trên), không bằng đổi font.

### Icon

Lưới 24, nét 1.8, đầu nét `Butt`, góc `Miter`, vẽ bằng code. Icon là nét viền, không tô đặc (ngoại lệ: glyph ⋮). Màu icon do nơi dùng truyền vào qua `tint`.

## 3. Bố cục màn hình

```
┌──────────────────────────────┐
│ Header (+ accent line 2dp)    │  Cancel | Tiêu đề | OK     hoặc  Tiêu đề
├──────────────────────────────┤
│                              │
│ Nội dung                     │
│                              │
├─────────────┬────●────┬──────┤
│ ☰  act      │ primary │ act ⋮│  Bottom bar
└─────────────┴─────────┴──────┘
   ← Drawer điều hướng (trái)        Menu (phải) →
```

- Một màn hình dùng `Bb10Scaffold`. Title bar, nội dung, bottom bar là cố định; hai panel trượt phủ lên trên.
- Bottom bar nằm đè lên đáy vùng nội dung. Nội dung chỉ được chừa chỗ bằng chiều cao phần thanh nhìn thấy; phần nút primary nhô lên che nội dung là chủ ý.

### Header (title bar)

- **Mọi màn hình đều có header**, thường là tên app hoặc tên màn. `Bb10Scaffold(title = ...)` bắt buộc truyền `title`; không có màn hình không header.
- Cao `titleBarHeight`, nền `surfaceVariant`, đường accent 2dp bên dưới.
- Mặc định tiêu đề căn trái. Khi có `dismissAction` hoặc `acceptAction` thì tiêu đề căn giữa giữa hai ô cố định 96dp.
- **`subtitle`** (ngày giờ, số lượng...) nằm **ngay dưới tiêu đề**, căn trái, chữ `secondary` màu phụ; header vẫn cao `titleBarHeight`. Thông tin phụ của một item (giờ nhận tin) đặt ở đây, không đặt trong nội dung.
- **Tab toàn cục** (`tabs`): thay cho tiêu đề trong header, xem mục Tab.
- **Cancel / Back ở trái, OK / Send / Save ở phải**, chữ accent kiểu `button`. Back có thêm icon chevron.
- Hai action này là nơi duy nhất cho xác nhận và hủy của màn hình. Không đặt Cancel/OK trong bottom bar hay trong nội dung.
- Action không dùng được thì `enabled = false` (giảm alpha), không ẩn đi.

**Tìm kiếm nằm trong header.** Không đặt ô tìm kiếm trong nội dung danh sách.
- Khi tìm kiếm (`search = Bb10Search(...)`), header đổi thành: ô nhập (nền `field`, placeholder nghiêng) | divider dọc 1dp | nút Cancel (chữ accent). Đường accent bên dưới giữ nguyên.
- Ô nhập tự nhận focus khi xuất hiện. Back hoặc Cancel gọi `onClose`; app tự xóa `query` nếu không muốn giữ lại.
- Action Search ở bottom bar chỉ việc bật trạng thái tìm kiếm.

### Bottom bar

- Chiều cao phần thanh `actionBarHeight` + divider 1dp, nền `surfaceVariant`.
- **Một action primary tối đa** (`primary = true`; chỉ action primary đầu tiên được dùng). Vẽ là vòng tròn accent `primaryActionSize` ở giữa, icon `onAccent`.
- Primary nằm **cùng lề dưới với các action bên cạnh**, chỉ to hơn, nên phần dư mọc lên trên thanh. Không đẩy cả khối lên.
- **Primary luôn nằm đúng giữa thanh**, kể cả khi chỉ có một action khác: hai nửa trái/phải của cụm có chiều rộng bằng nhau.
- Các action còn lại **dồn thành cụm sát hai bên primary**, mỗi ô rộng tối đa 72dp. Không chia đều hết chiều ngang. Action dư (số lẻ, hoặc chỉ có một action) nằm **bên phải** primary cho thuận tay phải.
- Icon action 28dp, cách nhãn 4dp, nhãn kiểu `action` màu `textSecondary` (nhãn primary dùng `textPrimary`). Mọi nhãn cùng một đường cơ sở sát lề dưới.
- Không hiện gợi ý phím tắt trên bottom bar.
- Tối đa `maxVisibleActions` ô (mặc định 5), tính cả primary và nút ⋮. Vượt thì phần còn lại vào menu.
- **Menu là một nút ⋮ duy nhất ở ngoài cùng bên phải**, chỉ có icon, không nhãn (có `contentDescription` cho TalkBack).
- **Nút Back là chevron `<` và một vạch chéo mảnh màu `accent` chính là cạnh phải của nút** (dấu hiệu của BB10): nút rộng 56dp, cạnh phải nghiêng 10dp (đỉnh dài hơn đáy), vùng highlight khi focus/nhấn (nền accent tối hơn, chevron trắng) có hình thang, phủ kín từ mép trái **tới tận vạch chéo**. Nút ☰ thì là ô chữ nhật bình thường. **Bottom bar không action** (chỉ hai ô góc) thì Back chỉ có chevron trong ô vuông, **không vẽ vạch**.
- **Đầu trái là một nút icon duy nhất: nút điều hướng ☰ (khi có `navigationItems`) *hoặc* nút Back (khi truyền `onBack`)**, không bao giờ cả hai; Scaffold từ chối nếu truyền cả hai. Hai đầu luôn được chừa chỗ bằng nhau (nút hoặc khoảng trống) để primary vẫn nằm giữa.
- **Màn hình đi sâu từ một màn khác dùng Back ở bottom bar** (không đặt Back ở header). Phím Back của hệ thống cũng gọi `onBack` khi không có menu/tìm kiếm đang mở.
- **Không khai báo action nào** (chỉ có ☰/Back và menu ⋮): bottom bar biến mất; còn hai ô vuông mờ nhỏ (cạnh `actionBarHeight`) ở hai góc dưới, phần giữa trống và nội dung không bị chừa chỗ cho thanh.
- Chỉ đưa vào bottom bar các hành động của màn hình hiện tại. Cài đặt, About, đổi theme… để trong menu.

### Menu phải và drawer trái

Hai panel khác nhau về cách xuất hiện:

**Menu bên phải** (`Bb10SidePanel`) trượt đè lên app:
- Rộng `min(80% màn hình, 320dp)`, cao hết màn hình, nền `surface`.
- Là nơi duy nhất cho các action không vừa bottom bar (Delete, Share, Settings...): các `Bb10LinkItem` xếp sát đáy, có icon và nhãn. Action nào tác động lên một item của list (như Delete) cũng nằm ở đây.
- **Ba trạng thái**: ẩn, *peek* (chỉ thò ra 13dp, tương đương khoảng 2mm, **app đã được làm tối ngay lúc này** bằng `scrim`), mở hẳn (đã tối, nhận focus).
- **Giữ một item của list** (touch): sau thời gian long-press, menu *peek* vào và **nền tối ngay** trong lúc còn giữ; **thả tay thì menu trượt hẳn vào**. Thả ra trước khi hết thời gian long-press hoặc kéo (cuộn) thì không có gì xảy ra. Thả tay sau khi giữ không tính là click.
- Phím Menu trên item hoặc trên màn hình mở hẳn ngay (không peek).
- Trượt vào 180ms, ra 120–150ms; nền mờ fade 150ms.
- Focus bị giữ trong menu khi mở và rơi vào mục đầu (được tô highlight accent tối, chữ + icon trắng), rồi d-pad lên/xuống đi qua các mục. **Kể cả khi menu vừa mở bằng cách giữ ngón tay**: sau một cú chạm cửa sổ ở chế độ touch, nơi các phần tử bấm được từ chối focus lập trình, nên panel phải đòi chế độ bàn phím (`focusForKeys`) trước khi đặt focus. Đóng xong focus quay lại nút ⋮.
- Đóng bằng: Back, phím Menu, chạm vào nền mờ, vuốt menu về phía mép (vuốt trên chính panel).
- **Gợi ý phím tắt** (chỉ khi có bàn phím vật lý): chữ `caption` xám nhạt (màu phụ, alpha 70%) ở **góc trên bên phải** của mục, không căn giữa theo chiều dọc và không dùng màu accent. Phím Delete / Backspace hiện là "Del".

**Drawer điều hướng bên trái** (`Bb10NavItem`) nằm phía sau app, **đẩy toàn bộ UI của app sang phải** khi mở:
- Mở bằng nút ☰ ở bottom bar. Nút này **không nằm ở header**.
- Rộng `min(80% màn hình, 320dp)`, nền `surface`. **App được làm tối** (`scrim`, đậm dần theo độ đẩy); phần app còn lại ở mép phải vẫn nhìn thấy, ngăn cách bằng đường divider 1dp.
- Animation đẩy 200ms. Trong lúc mở, app chỉ còn là vùng để đóng: chạm vào app hoặc vuốt sang trái thì đóng.
- Các mục xếp từ trên xuống, mục đang chọn tô nền accent, chữ `onAccent`; có thể có phụ đề và số đếm dưới icon.
- Focus bị giữ trong drawer (`exit = Cancel`), rơi vào mục đang chọn. Đóng xong focus về phần tử đầu tiên của app.
- **Không dùng `focusProperties { canFocus = ... }` trên phần app để khóa focus**: nó làm hỏng điều hướng d-pad (xem mục Bàn phím).

Chung: **không có cử chỉ vuốt từ mép trái/phải để mở** (trên Android đó là cử chỉ Back của hệ thống); mở bằng nút ☰ / ⋮, phím Menu hoặc giữ một item. Mở panel này thì đóng panel kia.

## 4. Component

### Focus (`bb10Clickable`)

Mọi thứ focus được hoặc bấm được phải đi qua `Modifier.bb10Clickable`. Không dùng `clickable` trực tiếp.

- Focus (highlight bằng d-pad): nền là **accent tối hơn**: **gradient `selection`** cho thanh, **màu phẳng `focusFill`** cho dòng (xem bảng màu), đặc, thay cho nền của chính phần tử, ở cả dark lẫn light. **Chữ và icon trong phần tử đó chuyển sang trắng** (`onAccent`). **Không vẽ viền (outline)** quanh nút khi focus. (Ô nhập và nút tròn của thanh trượt có chỉ báo focus riêng; cột picker tô accent 28% để chữ vẫn đọc được.)
- Pressed: nền `pressedFill` (accent tối hơn nữa), nội dung cũng trắng.
- Enter / d-pad center kích hoạt (từ `combinedClickable`), Space cũng kích hoạt.
- Phím Menu gọi `onLongClick` nếu có (context menu).
- `enabled = false`: alpha 0.4 và không nhận sự kiện.
- `circle = true` cho nút tròn: nền focus vẽ dạng tròn; `slant` cho nút Back (hình thang).
- **Đổi màu nội dung khi highlight**: tạo `val highlight = rememberBb10Highlight()`, truyền `highlight = highlight` cho `bb10Clickable`, và bọc nội dung trong `Bb10OnHighlight(highlight) { ... }`. Trong đó mọi `Bb10Text` / `Bb10Icon` được ép màu trắng, bất kể màu đã truyền. Nếu nền highlight cần vẽ ở chỗ khác với vùng chạm (thanh dropdown 40dp trong vùng chạm 48dp) thì dùng `paintHighlight = false` và `Modifier.bb10HighlightFill(highlight)` ở đúng phần tử có nền.
- Mở một menu / hộp thoại / picker rồi đặt focus vào nó thì dùng `LocalInputModeManager.current.focusForKeys(requester)` (nội bộ của kit), không gọi `requestFocus()` trần: sau khi chạm, chế độ touch làm lệnh đó không có tác dụng và d-pad không đi được.

### Toggle (`Bb10Toggle`)

- Cả dòng là vùng focus. Nhãn (+ phụ đề) bên trái, công tắc bên phải.
- **Highlight không tô cả dòng**: khi focus (hoặc nhấn) chỉ có **nút tròn có thêm vòng accent trong suốt** (accent 45%, dày 5dp, nằm ngoài nút và ngoài thân công tắc).
- Công tắc viên thuốc 52x28dp, nút tròn trắng 22dp, chuyển 120ms.
- **Bật**: nền accent, nút ở bên phải, trong nút có **"I"** (vạch dọc accent, nét 1.25dp, cao 7dp).
- **Tắt**: nền xám (`#5A5A5A` dark, `#BDBDBD` light), nút ở bên trái, trong nút có **"O"** (vòng xám `#8A8A8A`, bán kính 3.25dp, nét 1.25dp).
- Glyph phải nhỏ và mảnh; không làm to hay đậm hơn.

### Thanh cuộn (`Bb10LazyColumn`, `bb10Scrollbar`)

- Mọi danh sách và trang cuộn được đều có **thanh cuộn mảnh 3dp ở mép phải**, màu `textSecondary` 60%, không bo góc, ngắn nhất 24dp.
- Hiện khi đang cuộn và **mờ dần sau khoảng 0,9 giây** khi dừng (vào 80ms, ra 400ms). Dropdown mở ra giữ thanh cuộn luôn hiện vì đó là dấu hiệu duy nhất cho thấy còn lựa chọn.
- Dùng `Bb10LazyColumn` thay cho `LazyColumn`; trang là `Column` cuộn thì dùng `Modifier.bb10VerticalScroll(state)`. Vị trí thanh là ước lượng theo chiều cao trung bình của các dòng.

### Làm mới dữ liệu

- **Không có kéo để làm mới.** Làm mới là **một mục trong menu bên phải** (icon `Refresh`, nhãn "Làm mới"), hoặc một action trong bottom bar nếu đó là việc chính của màn hình. Kit không cung cấp component pull-to-refresh.

### Chuyển màn hình (`Bb10PushHost`)

- Mở một màn từ danh sách (bấm một item): **màn mới trượt vào từ mép phải** đè lên, **màn bên dưới tối đi** bằng `scrim` (giống cách context menu hiện ra). Quay lại thì màn trượt ra về bên phải và nền sáng lại.
- Màn bên dưới vẫn còn trong composition (giữ vị trí cuộn, focus, state); chạm vào nó bị chặn trong lúc có màn trượt.
- Focus được giữ trong màn mới (d-pad không đi lạc ra phía sau); khi đóng, focus trở về **đúng item đã mở** màn đó.
- Màn trượt vào dùng `onBack = { ... }` trên scaffold của nó: nút Back ở bottom bar và phím Back hệ thống cùng đóng nó. Thời gian: vào 240ms, ra 200ms.
- Không dùng cho tab hay mục của drawer (đó là chuyển ngang, không có animation).

### Chọn nhiều (multi-select trong list)

- Vào chế độ: **giữ một item**, mở menu phải, chọn "Chọn thêm" (action của app). Item đã giữ được chọn sẵn.
- Dòng được chọn **trượt nhẹ sang phải** để lộ một dải `accent` 44dp có dấu tick trắng, đồng thời nền dòng đổi sang **sắc `accent` nhạt** (18%). Bỏ chọn thì trượt về bên trái và nền trở lại ban đầu (150ms). Dòng chưa chọn giữ nguyên.
- Bấm một dòng trong chế độ này là đổi trạng thái chọn của dòng đó (không mở). `Role.Checkbox` + trạng thái bật/tắt.
- **Bottom bar**: đầu trái là **Hủy** (icon vòng tròn gạch chéo + nhãn "Hủy") thay cho ☰/Back; hành động riêng cho các dòng đã chọn nằm giữa thanh (một số hành động đặc biệt, ví dụ "Đánh dấu chưa đọc"), phần còn lại trong menu ⋮. Phím Back cũng thoát chế độ.
- Header và nội dung giữ nguyên; app thay `actions` của Scaffold khi `selectionMode = true`.

### Chọn ngày giờ (`Bb10DatePicker`, `Bb10TimePicker`)

- Các **cột cuộn dọc** cạnh nhau (ngày | tháng | năm, hoặc giờ | phút | SA/CH), mỗi cột 5 dòng cao 40dp. Vùng chọn ở giữa chỉ là **hai đường kẻ accent mảnh** (không có nền riêng); cuộn tự khớp vào dòng.
- **Đường kẻ dọc giữa các cột gồm hai đoạn: một đoạn phía trên và một đoạn phía dưới vùng chọn** (mỗi đoạn dài 2 dòng, chừa khoảng trống 8dp ở mép ngoài và 6dp sát vùng chọn). **Không có đoạn nào nằm trong vùng chọn**: ở đó chỉ có hai đường kẻ accent chạy ngang. Các đường kẻ do chính cột vẽ ở mép phải của nó (không có dải riêng giữa hai cột), nên nền các cột liền mạch với nhau và không lộ nền phía sau ra giữa các cột.
- Chữ số luôn cùng kiểu (không đổi đậm/nhạt, không đổi kích thước). Dòng thứ hai (thứ trong tuần dưới ngày, tên tháng dưới tháng) **luôn được chừa chỗ, mờ dần vào khi dòng tiến gần vùng chọn và mờ dần ra khi đi xa** (cách một dòng là hết), nên cuộn không làm bố cục giật.
- Vuốt/kéo hoặc chạm một dòng để chọn. D-pad: lên/xuống đổi một dòng (ở dòng đầu/cuối thì cho focus rời khỏi); trái/phải chuyển cột.
- **Enter / Space / d-pad center = "xong"** (xử lý khi nhả phím, vì nếu xử lý khi nhấn thì focus nhảy sang nút khác và phím nhả sẽ bị nút đó hiểu là một cú bấm):
  - **trong dropdown**: giá trị được giữ lại (picker áp dụng giá trị ngay khi cuộn) và **dropdown đóng**, focus về lại thanh dropdown;
  - **trong popup**: **không tự xác nhận và không đóng popup**, mà **chuyển focus sang nút OK**; bấm Enter lần nữa trên OK mới xác nhận. Khi popup mở, focus nằm sẵn trên picker.
- Ngày tự co lại khi tháng ngắn hơn (31/1 → tháng 2 thành 28). Phút có `minuteStep`; giờ 12/24 giờ.
- **Tên tháng, thứ, SA/CH và mẫu hiển thị ngày giờ là string resource của kit** (`bb10_months`, `bb10_weekdays`, `bb10_am_pm`, `bb10_date_pattern`...), không lấy từ locale ngày tháng của thiết bị, để cùng ngôn ngữ với phần còn lại của giao diện.
- **Chỗ đặt linh hoạt**: cùng một picker dùng được
  - **trong dropdown** (`Bb10DatePickerField`, `Bb10TimePickerField`: thanh dropdown hiện giá trị, mở ra thì picker xổ ngay trong dòng, focus vào picker);
  - **trong popup** (`Bb10DatePickerDialog`: nút Hủy | Hôm nay | Đồng ý; `Bb10TimePickerDialog`: Hủy | Đồng ý), hoặc đặt thẳng trong trang.
- Cột đơn lẻ `Bb10PickerColumn` dùng cho mọi giá trị chọn từ danh sách (thời lượng, số lượng...).
- `Bb10DropdownPanel` là khung chung của mọi dropdown: thanh xám, mở ra thì xổ nội dung bất kỳ.

### Tab (`Bb10TabBar`, `tabs`)

Hai loại, cùng một diện mạo:
- **Tab cục bộ** (`Bb10TabBar`): dải tab nằm **ngay dưới header** của màn hình, đổi giữa các góc nhìn *của màn hình hiện tại* (Tác vụ / Ghi chú). Có divider đóng dải.
- **Tab toàn cục** (`Bb10Scaffold(tabs = ...)`): các phần cấp cao nhất của app (Máy tính / Tiền thưởng / Chuyển đổi), **thay chỗ tiêu đề trong header**; đường accent dưới header giữ nguyên. `title` vẫn phải truyền (tên của header cho trình đọc màn hình) nhưng không được vẽ.
- Các tab chia đều chiều ngang, **cao 40dp** cả ở tab cục bộ lẫn trong header (gọn, không padding thừa trên dưới), chữ `button` **in đậm**.
- Tab đang chọn: nền sáng hơn (`background`), chữ `accent`. Tab còn lại: nền xám (`surfaceVariant`), chữ `textPrimary`; hai tab kề nhau cùng chưa chọn có đường kẻ dọc 1dp ngăn cách.
- `Role.Tab` + `selected`. D-pad trái / phải đi giữa các tab, Enter chọn. Đổi tab không tạo màn hình mới (không nằm trong back stack).
- Dùng hai loại cùng lúc được (tab toàn cục ở header, tab cục bộ trong một mục), nhưng tránh lồng quá hai tầng.

### Checkbox (`Bb10Checkbox`)

- Một dòng như list item (cao tối thiểu `listItemMinHeight`, divider bên dưới): icon tùy chọn, nhãn + phụ đề, **ô vuông 26dp ở cuối**.
- **Icon đổi màu theo trạng thái**: đang chọn thì tô `accent`, không chọn thì màu chữ thường (`textPrimary`: trắng ở dark). Không truyền màu riêng cho từng icon.
- Chọn: ô vuông tô `accent` với dấu tick trắng. Không chọn: ô vuông xám phẳng (`#4A4A4A` dark, `#D0D0D0` light), không viền, không bo góc.
- Cả dòng là vùng focus và vùng chạm; Enter / Space / center đổi trạng thái. `Role.Checkbox` + trạng thái bật/tắt.
- **Highlight tô cả dòng** (accent tối hơn, chữ + icon trắng); trên dòng đó ô vuông chuyển thành **ô trắng trong suốt** (trắng 25%) với dấu tick trắng nếu đang chọn.
- Dùng cho danh sách chọn nhiều hoặc cài đặt dạng "có / không có" (cài đặt nhanh). Công tắc bật/tắt một tính năng thì dùng `Bb10Toggle`.

### Drop-down (`Bb10Dropdown`)

- Đóng: thanh xám (`field`) **cao 40dp** (gọn; vùng chạm vẫn 48dp): nhãn **in đậm (Bold)** ở đầu, giá trị đang chọn ở cuối (`textSecondary`) và chevron xuống.
- Mở: thanh đổi thành **accent** (chữ `onAccent`, giá trị ẩn) và các lựa chọn **xổ từ từ xuống ngay bên dưới, trong dòng** (không phải popup, 220ms; thu lại 180ms): mỗi lựa chọn là một dòng **cao bằng thanh (40dp)** có divider, dấu tick ở cuối dòng đang chọn.
- Lựa chọn **chưa chọn nền xám** (`surfaceVariant`), lựa chọn **đang chọn nền sáng nhất** (trắng ở light, `divider` ở dark). Danh sách tối đa 4,5 dòng rồi cuộn, có thanh cuộn mảnh 3dp ở mép phải.
- **Chevron xoay 180° ngược chiều kim đồng hồ khi mở và theo chiều kim đồng hồ khi đóng** (220ms), cùng lúc với danh sách.
- **Tự cuộn trang cho vừa màn hình**: mở một dropdown (kể cả picker) nằm gần cuối trang cuộn được thì trang tự cuộn lên trong lúc nội dung xổ ra, sao cho cả thanh lẫn phần xổ ra đều nằm trong màn hình (nằm trên bottom bar).
- Chọn một dòng thì gọi `onSelect` và đóng lại. Back hoặc bấm lại thanh thì đóng, không đổi giá trị. Focus rơi vào lựa chọn đang chọn; d-pad lên xuống đi giữa các lựa chọn.
- `Role.DropdownList`, trạng thái "Expanded / Collapsed" đọc qua `stateDescription` (string resource); lựa chọn đang chọn có `selected`.

### Stepper (`Bb10Stepper`)

- **Giống hệt slider** (đường accent mảnh 2dp, nút tròn xám 24dp, icon tùy chọn ở hai đầu) nhưng chọn **số nguyên theo nấc**: có **vạch nhỏ 8dp ở mỗi nấc** trên đường, và nút tròn **nhảy khít từng nấc** khi kéo (90ms) thay vì trượt tự do.
- Chạm vào đường thì nhảy tới nấc gần nhất; d-pad trái/phải đổi đúng một nấc; giá trị vượt biên thì bị kẹp.
- Nhãn truyền qua `label` cho trình đọc màn hình. Cùng semantics với slider (khoảng giá trị, số nấc, `setProgress`).
- Dùng khi chỉ có vài giá trị rời rạc (mức rung 0–4, số sao 1–5); giá trị liên tục thì dùng `Bb10Slider`.

### Slider (`Bb10Slider`)

- Một đường **accent mảnh 2dp** suốt chiều ngang với **nút tròn xám** 24dp (không có phần đã/chưa điền hai màu); tùy chọn icon ở hai đầu (như loa nhỏ và loa to).
- Chạm hoặc kéo để đổi giá trị. Khi focus, d-pad **trái / phải** đổi 5% khoảng giá trị (hoặc một nấc nếu có `steps`); lên / xuống vẫn chuyển focus. Khi focus, nút tròn xám có **vòng accent trong suốt** (accent 45%, dày 6dp) quanh nó; không tô cả dòng.
- `steps` = số nấc nằm giữa hai đầu; giá trị được hút vào nấc gần nhất.
- Không có nhãn hiển thị nên phải truyền `label` cho trình đọc màn hình. Semantics: `progressBarRangeInfo` và hành động `setProgress`.

### Text field (`Bb10TextField`)

- **Label nằm phía trên** ô nhập, kiểu `secondary`, `textPrimary`; trường bắt buộc thêm " *" sau label.
- Ô nhập nền phẳng `field`, cao tối thiểu `touchHeight`, không bo góc, không gạch chân.
- Placeholder in nghiêng, `textDisabled`.
- Lề ngoài: ngang `spaceL`, dọc `spaceS`; field tự lo padding, nơi dùng không bọc thêm.
- Chữ gõ vào được ô nhập nuốt, nên phím tắt một chữ cái của Scaffold không chạy khi đang gõ.
- **Hai cấp focus** (để d-pad không bị kẹt trong ô nhập, nơi phím mũi tên thành phím điều khiển con trỏ):
  - D-pad đi tới ô nhập thì **chỉ highlight** (viền accent), ô chưa nhận focus, chưa có con trỏ; mũi tên vẫn đi tiếp được.
  - **Vào ô để gõ** bằng Enter / Space / d-pad center (xử lý khi nhả phím), chạm vào ô, hoặc **gõ một chữ** (chữ đó được giữ lại).
  - Đang gõ: trái / phải chạy con trỏ, nhưng ở đầu / cuối chữ thì chuyển focus sang phần tử bên cạnh (ví dụ nút con mắt); lên / xuống rời ô (ô nhiều dòng thì chỉ rời ở dòng đầu / dòng cuối). Enter ở ô một dòng, nút Done của bàn phím và Back đều kết thúc việc gõ.
  - `startEditing = true` cho ô duy nhất mà màn hình sinh ra để gõ (form soạn thư, ô của hộp thoại nhập): con trỏ có sẵn trong ô. `onDone` cho biết focus đi đâu sau khi gõ xong (ví dụ nút OK, hoặc ô kế tiếp). `focusRequester` chỉ đưa focus tới ô (highlight), không bắt đầu gõ.

**Trạng thái** (mỗi trạng thái có test trong `TextFieldStatesTest`):

| Trạng thái | Hiển thị | Ghi chú |
|---|---|---|
| Bình thường | nền `field` | |
| Focus | viền accent 2dp | |
| `error = "..."` | viền `error` 2dp (kể cả khi không focus) và thông điệp chữ `caption` màu `error` bên dưới | Thay thế `helper`. Được đọc bằng `error()` của semantics. Thông điệp nói rõ cách sửa, không chỉ báo "sai". |
| `helper = "..."` | chữ `caption` màu phụ bên dưới | Chỉ hiện khi không có lỗi. |
| `enabled = false` | mờ 50%, chữ `textDisabled`, không focus được | |
| `readOnly = true` | giống bình thường | Focus và chọn chữ được, không sửa được; không có nút xóa. |
| `maxLength = n` | bộ đếm `đã gõ/n` ở góc phải dưới | Chữ gõ quá giới hạn bị bỏ qua. |
| `clearable = true` | nút X ở cuối khi ô có chữ | Chỉ khi sửa được; không dùng cùng `password`. Có tên "Clear". |
| `password = true` | che chữ, nút mắt ở cuối (ô vuông `touchHeight`, `textSecondary`) | Bấm để hiện / ẩn. |

- Nhiều dòng: `singleLine = false`, `minLines`.
- Hành động của bàn phím ảo (Next, Search, Done...) qua `keyboardOptions` / `keyboardActions`.
- Lỗi chỉ hiện sau khi người dùng đã nhập hoặc rời ô, không hiện ngay lúc màn hình vừa mở (do app quyết định khi truyền `error`).

### List item (`Bb10ListItem`)

- Cao tối thiểu `listItemMinHeight`, nền `background`, divider 1dp bên dưới.
- Tiêu đề `body`, phụ đề `secondary` `textSecondary`, cả hai một dòng, cắt bằng dấu "…".
- Slot đầu và cuối cho marker / giờ. `selected` vẽ vạch accent 3dp bên trái.
- `onFocused` báo khi dòng nhận focus; `onLongClick` và phím Menu mở context menu.
- `Bb10Marker`: ô vuông 8dp accent cho "chưa đọc"; ẩn bằng trong suốt để giữ chỗ.
- `Bb10ListHeader`: chữ HOA kiểu `header`, `textSecondary`, đường accent 1dp bên dưới, nền `surfaceVariant` **hơi trong suốt (88%)**: dùng làm header dính (`stickyHeader`) thì các dòng cuộn bên dưới lộ mờ ra phía sau.

### Link item (`Bb10LinkItem`)

Dòng điều hướng / cài đặt: dùng trong drawer, menu phải và danh sách cài đặt.

- Icon ở đầu (có thể tô màu riêng), tiêu đề `body`, phụ đề `secondary` tùy chọn.
- Số đếm (`badge`) nằm dưới icon, kiểu `caption`.
- `hint` (gợi ý phím) ở góc trên bên phải của dòng, `caption`, xám nhạt (không phải accent).
- Divider 1dp bên dưới, chiều cao tối thiểu `listItemMinHeight`.
- `selected`: tô nền accent, chữ và icon `onAccent`.
- Không có mũi tên chevron; cả dòng là liên kết.

### Button (`Bb10Button`)

- **Giống thanh của dropdown nhưng không có chevron**: thanh phẳng nền `field`, cao 40dp trong vùng chạm 48dp, không bo góc, **chữ `button` đậm, căn giữa**; lề hai bên `spaceL`, lề dọc `spaceXs` (xếp xen kẽ với dropdown thì đều nhau).
- **Mặc định rộng hết hàng** (như nút "Cài đặt màu hiển thị" của trang Hiển thị hệ thống). Đặt nhiều nút trong một `Row` thì cho mỗi nút `Modifier.weight(1f)`; tối thiểu 88dp.
- `primary = true`: nền accent, chữ `onAccent`. Còn lại: nền `field`, chữ `textPrimary`.
- Focus / nhấn: thanh đổi sang accent tối hơn, chữ trắng (như mọi phần tử bấm được).
- Nút trong nội dung dùng khi thật sự cần; hành động xác nhận của màn hình thuộc title bar, hành động chính thuộc bottom bar.

### Dialog (`Bb10Dialog`)

Alert (một nút) và popup hai nút dùng chung:
- Hộp phẳng nền `surface`, rộng 85% màn hình (tối đa 420dp), căn giữa, không bo góc, không đường accent phía trên.
- Phần trên: tiêu đề kiểu `title` (tối đa 2 dòng), thông điệp `body` `textSecondary`, padding `spaceL`.
- **Nút nằm trong một hàng full-width sát đáy hộp**, không padding, không bo góc, cao `touchHeight + 4dp`:
  - **Một nút** (alert): nút accent chiếm toàn chiều ngang, chữ `onAccent`.
  - **Hai nút** (popup hỏi): hai nút **trung tính** (nền `surfaceVariant`, chữ `textPrimary` in đậm) chia đôi chiều ngang, divider 1dp ở giữa và phía trên hàng nút. Hủy bên trái, xác nhận bên phải. Không dùng màu accent đặc cho nút xác nhận.
- Có thể có thêm **nội dung ở giữa** (picker, ô nhập) và **nút thứ ba ở giữa** hai nút (ví dụ "Hôm nay"): ba nút chia đều, đều trung tính.
- Phía sau là `scrim` phủ toàn màn hình.
- Focus ban đầu ở nút xác nhận: Enter xác nhận, d-pad trái sang hủy.
- Đóng bằng Back hoặc chạm ra ngoài hộp.
- Dùng cho thông báo và xác nhận ngắn. Form nhiều trường là màn hình riêng có Cancel/OK ở header, không phải dialog.
- `confirmEnabled = false` làm nút xác nhận mờ (alpha 0.4) và không bấm được (form chưa hợp lệ).

### Dialog có ô nhập (`Bb10InputDialog`)

- Hộp thoại **một ô nhập** (đổi tên, nhập mã): tiêu đề, thông điệp tùy chọn, ô nhập, hai nút Hủy | OK.
- **Con trỏ có sẵn trong ô khi hộp mở**, gõ được ngay. **Enter kết thúc việc gõ và chuyển focus sang nút OK** (Enter lần nữa mới xác nhận), cùng quy tắc với picker trong popup.
- OK mờ đi khi `confirmEnabled` sai; mặc định là khi có `error`.
- Nhiều ô (mật khẩu mới + xác nhận): dùng `Bb10Dialog(focusContent = true, confirmEnabled = ...)` với các ô làm `content`; ô đầu `startEditing = true`, mỗi ô `onDone` chuyển focus sang ô kế, ô cuối `onDone = focusConfirm`.

### Bàn phím và phím tắt

- Action có thể có `shortcut`; chạy khi không có phần tử sâu hơn (ví dụ text field) nuốt phím, và không có Ctrl/Alt/Meta.
- **Delete dùng phím vật lý Del**, không phải chữ D. Phím Del của bàn phím BlackBerry là Android DEL (`Key.Backspace`); kit coi `Key.Delete` và `Key.Backspace` là một, và gợi ý phím hiện "Del".
- Phím Menu bật/tắt menu phải; Back đóng panel đang mở.
- Khi vào màn hình, focus tự nhảy tới phần tử focus được đầu tiên để phím hoạt động trước lần chạm đầu tiên (`autoFocus`). Màn hình có trường nhập chính thì cho trường đó `startEditing = true` (con trỏ có sẵn) và tắt `autoFocus`.
- Sau khi xóa phần tử đang focus, phải đặt lại focus vào một phần tử còn tồn tại.
- D-pad xuống từ header phải vào phần tử focus được đầu tiên của nội dung, rồi mới tới bottom bar. Điều này có test (`ScaffoldBehaviourTest`); mọi thay đổi về focus (`focusProperties`, `focusGroup`, `focusRequester`) phải chạy lại test này.
- Giữ một item bằng touch không được kích hoạt click khi thả tay.

## 5. Khả năng tiếp cận (a11y)

Các điểm dưới đây đã có sẵn trong kit và có test (`AccessibilityAndLayoutTest`). Component mới phải giữ đúng.

| Quy tắc | Cách thực hiện |
|---|---|
| Vùng chạm tối thiểu 48dp, ở mọi cỡ hiển thị | Token `minTouchTarget`; `Bb10Dimens.touchHeight` cho control. Test duyệt mọi phần tử bấm được. |
| Nút chỉ có icon phải có tên | Nút ☰ và ⋮ có `contentDescription` từ `Bb10Strings`; nút primary có tên của action; nút mắt đọc "Show/Hide password". Icon đứng một mình truyền `contentDescription` vào `Bb10Icon`; icon cạnh nhãn chữ thì để trống (trang trí). |
| Tiêu đề là heading | Tiêu đề header, tiêu đề dialog, `Bb10ListHeader` có `heading()`. |
| Trạng thái được đọc ra | Toggle: `Role.Switch` + trạng thái bật/tắt; mục đang chọn (`LinkItem`, `ListItem`, bước cỡ hiển thị) có `selected`; trạng thái chỉ thể hiện bằng hình (như "chưa đọc" của `Bb10Marker`) truyền qua `stateDescription`. |
| Trường nhập có tên | Label hiển thị nằm ngoài ô, nên ô nhập được gán label làm `contentDescription`; trường bắt buộc đọc thêm "required". Ô tìm kiếm ở header dùng placeholder làm tên. |
| Panel báo tên | Menu phải, drawer và dialog có `paneTitle`. |
| Disabled | Dùng `enabled = false` (alpha 0.4, semantics disabled), không tự vẽ lại. |
| Điều hướng được bằng bàn phím | Xem mục Bàn phím: focus nhìn thấy, d-pad đi hết, Back đóng panel. |
| Cỡ chữ và cỡ hiển thị | Cỡ hiển thị của kit nhân dp và sp; cỡ chữ hệ thống vẫn được tôn trọng. Thử ở `scale = 2`. |
| Dịch được | Mọi chữ do chính kit hiển thị hoặc đọc ra là string resource `bb10_*` (xem mục Chuỗi). |

Chữ của app (nhãn action, tiêu đề, placeholder...) do app đặt và tự dịch; kit chỉ lo chữ của riêng nó.

## 6. Chuỗi (string resource)

- Mọi chữ do **chính kit** hiển thị hoặc đọc ra nằm trong `res/values/strings.xml` với tên `bb10_*`; đã có bản `values-vi`. Component không viết chữ cứng.
- Kit gồm: tên nút ☰ và ⋮, "Back", "Cancel", "Search" mặc định của header tìm kiếm, hiện/ẩn mật khẩu, "Clear", "required", nhãn và tên các bước của cỡ hiển thị, định dạng bộ đếm ký tự.
- `Bb10Theme` đọc chúng qua `rememberBb10Strings()` nên đổi theo ngôn ngữ thiết bị (và đổi khi cấu hình thay đổi). Truy cập trong component bằng `Bb10.strings`; chuỗi có định dạng thì dùng `stringResource`.
- Thêm ngôn ngữ: thêm `values-xx/strings.xml` cùng tên vào kit, hoặc vào app (app thắng). Ghi đè từng mục không cần file: `Bb10Theme(strings = rememberBb10Strings().copy(moreActions = "..."))`.
- Chữ **của app** (nhãn action, tiêu đề, placeholder, thông điệp lỗi...) do app đặt và tự dịch; truyền vào component dưới dạng `String`. **Đặt mọi chữ của app trong string resource** (không viết chuỗi cứng trong code) để kit và app luôn cùng ngôn ngữ; demo của repo này làm như vậy (`values/` và `values-vi/`).
- Thêm chuỗi mới: thêm vào cả `values/` và `values-vi/`, thêm trường vào `Bb10Strings` nếu cần dùng ở nhiều nơi, và test bằng `@Config(qualifiers = "vi")`.

## 7. Thêm component mới

Kiểm tra trước khi merge:

1. Màu, khoảng cách, cỡ chữ lấy từ token; không có số cố định mới nào ngoài các hằng đã nêu ở đây (nếu cần thì thêm token).
2. Mọi phần tử tương tác dùng `bb10Clickable` và có `role` đúng.
3. Dùng được trọn vẹn bằng d-pad: tới được, thấy focus, Enter kích hoạt, rời đi được.
4. Hiển thị đúng ở cả dark / light, cả Regular / Compact, và ở `scale` 0.85 đến 2.
5. Disabled dùng alpha 0.4, không có màu riêng.
6. Component điều hướng hoặc xác nhận đặt đúng chỗ theo mục 3 (Cancel/OK ở title bar, primary ở bottom bar, menu phụ ở panel phải, điều hướng ở drawer trái).
7. Có KDoc nêu rõ hành vi phím nếu khác mặc định.
8. Đạt các quy tắc a11y ở mục 5 (tên, vùng chạm 48dp, heading, trạng thái); chữ kit tự hiển thị là string resource (mục 6) và có bản dịch `vi`.
9. Hành vi focus / cử chỉ / bố cục mới thì thêm test vào `ScaffoldBehaviourTest` hoặc `AccessibilityAndLayoutTest` (Robolectric, chạy bằng `./gradlew :bb10-kotlin-ui-kit:testDebugUnitTest`).
10. Phần tử bấm được đổi màu nội dung khi highlight (`Bb10OnHighlight`), và có test.
11. Thêm một ví dụ vào `catalog` (module `:catalog`), kể cả các trạng thái (disabled, selected, lỗi...).
12. Mỗi trạng thái của component có một ví dụ trong catalog và một test.
13. Cập nhật tài liệu này.
