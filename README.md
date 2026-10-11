# BB10 Kotlin UI Kit

Design system Jetpack Compose lấy cảm hứng từ giao diện BlackBerry 10. Không phụ thuộc Material, ưu tiên màn hình vuông (720x720) và điều khiển bằng d-pad / bàn phím vật lý, vẫn dùng tốt bằng cảm ứng.

- Phẳng, nét mảnh, một màu accent cho cả app
- Dark và light theme, 6 màu accent
- Tự chuyển sang kích thước "compact" trên màn hình gần vuông
- Icon vẽ bằng code, không dùng tài sản của BlackBerry

Quy tắc thiết kế của từng component nằm trong [DESIGN.md](DESIGN.md).

> Chưa kiểm thử trên máy thật. Code compile được; phần hiển thị, animation và focus bằng d-pad cần xem thử trên thiết bị.

## Có gì trong kit

| Nhóm | Component |
|---|---|
| Khung màn hình | `Bb10Scaffold` (header bắt buộc, ☰ hoặc Back ở đầu trái bottom bar), `Bb10TitleBar`, `Bb10TitleAction`, `Bb10Search` |
| Tab | `Bb10TabBar` (tab cục bộ dưới header), `Bb10Scaffold(tabs = ...)` (tab toàn cục thay tiêu đề) |
| Chuyển màn | `Bb10PushHost`: màn mới trượt vào từ phải và làm tối màn bên dưới |
| Ngày giờ | `Bb10DatePicker`, `Bb10TimePicker` (cột cuộn), `...Field` (trong dropdown), `...Dialog` (popup), `Bb10PickerColumn` |
| Chọn nhiều | `Bb10ListItem(checked = ...)`, `Bb10Scaffold(selectionMode = ...)` |
| Thanh action | `Bb10Action` (có `primary`), menu "⋮" trượt từ phải |
| Điều hướng | Drawer bên trái qua `Bb10NavItem`: nút ☰ ở bottom bar, đẩy và làm tối app |
| Danh sách | `Bb10ListItem`, `Bb10LinkItem`, `Bb10ListHeader`, `Bb10Marker`, `Bb10LazyColumn` (có thanh cuộn) |
| Form | `Bb10TextField` (label, bắt buộc, password, lỗi, helper, disabled, read-only, giới hạn ký tự, nút xóa), `Bb10Toggle`, `Bb10Checkbox`, `Bb10Dropdown`, `Bb10Slider`, `Bb10Stepper`, `Bb10Button` |
| Hộp thoại | `Bb10Dialog`: alert một nút, popup hai nút; `Bb10InputDialog`: một ô nhập |
| Nền tảng | `Bb10Theme(scale = ...)`, `Bb10.colors / .typography / .dimens`, `Modifier.bb10Clickable`, `Bb10Icons` |
| Cài đặt (bắt buộc) | `rememberBb10UiScale` + `Bb10ScalePicker`: cỡ hiển thị do người dùng chọn |
| Dịch | String resource `bb10_*` (English + Vietnamese), ghi đè bằng `Bb10Theme(strings = ...)` |

## Yêu cầu

- Kotlin 2.0 trở lên với plugin `org.jetbrains.kotlin.plugin.compose`
- `minSdk` 26, `compileSdk` 34
- Jetpack Compose (kit tự kéo Compose BOM `2024.12.01`)
- Android Studio Ladybug trở lên để build kit

## Cài đặt

Tọa độ thư viện: `duc1607:bb10-kotlin-ui-kit:0.1.0`.

### Cách 1: Maven local (dùng trên cùng một máy)

Publish kit vào `~/.m2`:

```bash
./gradlew :bb10-kotlin-ui-kit:publishToMavenLocal
```

Trong project dùng kit, `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
}
```

`app/build.gradle.kts`:

```kotlin
dependencies {
    implementation("duc1607:bb10-kotlin-ui-kit:0.1.0")
}
```

Mỗi lần sửa kit, tăng `version` trong `bb10-kotlin-ui-kit/build.gradle.kts` rồi publish lại để project khác không dùng nhầm bản cũ trong cache. Lệnh publish cần mạng.

### Cách 2: Nhúng module trực tiếp

Copy thư mục `bb10-kotlin-ui-kit` vào project, rồi:

```kotlin
// settings.gradle.kts
include(":bb10-kotlin-ui-kit")

// app/build.gradle.kts
dependencies {
    implementation(project(":bb10-kotlin-ui-kit"))
}
```

Project đó cần có các plugin Android library, Kotlin Android và Kotlin Compose như trong `gradle/libs.versions.toml` của repo này.

## Dùng nhanh

```kotlin
val scale = rememberBb10UiScale()

Bb10Theme(scale = scale, dark = true, accent = Bb10Accents.Blue) {
    Bb10Scaffold(
        title = "Contacts",                    // bắt buộc: mọi màn hình đều có header
        actions = listOf(
            Bb10Action("search", "Search", Bb10Icons.Search, Key.S, primary = true) { /* ... */ },
            Bb10Action("add", "Add", Bb10Icons.Add, Key.A) { /* ... */ },
            // vượt quá maxVisibleActions thì tự vào menu "⋮" bên phải
        ),
        // có menu điều hướng riêng thì truyền vào đây: drawer nằm sau app, đẩy app sang phải
        navigationItems = listOf(Bb10NavItem("All", Bb10Icons.Mail)),
        onNavigate = { /* ... */ },
        // Cancel / OK ở header; tìm kiếm cũng nằm ở header:
        // dismissAction = Bb10TitleAction("Cancel") { ... },
        // acceptAction = Bb10TitleAction("Save") { ... },
        // search = Bb10Search(query, onQueryChange = { ... }, onClose = { ... }),
    ) {
        // nội dung màn hình
    }
}
```

Mọi thứ focus được phải đi qua `Modifier.bb10Clickable` để có cùng hành vi d-pad (xem DESIGN.md).

## Cỡ hiển thị (bắt buộc)

Máy thật có màn từ 720x720 đến 1440x1440 và người dùng lớn tuổi cần giao diện to hơn, nên **mọi app dùng kit phải có cài đặt cỡ hiển thị**. Kit ép điều này ở mức API: `Bb10Theme` yêu cầu tham số `scale` (không có giá trị mặc định).

```kotlin
val scale = rememberBb10UiScale()     // lưu vào SharedPreferences

Bb10Theme(scale = scale) {
    // ... trong màn Settings của app:
    Bb10ScalePicker(scale)
}
```

- 5 bước: Small 0.85, Normal 1, Large 1.25, Extra large 1.5, Huge 2.
- Hệ số nhân tất cả dp và sp (layout, icon, chữ cùng lúc) bằng cách scale `Density`, nên mọi component to lên đồng đều và bố cục tự tính lại.
- `Bb10ScalePicker`: 5 chữ "A" lớn dần, tự vẽ theo cỡ hiện tại nên vẫn đọc được khi đã chọn cỡ lớn.
- `Bb10.uiScale` cho biết hệ số đang áp dụng.

## Chuỗi và ngôn ngữ

Chữ do chính kit hiển thị (tên nút ☰ và ⋮, Back, Cancel, Search, hiện/ẩn mật khẩu, Clear, tên các cỡ hiển thị...) là Android string resource `bb10_*`, có sẵn English và Vietnamese và đổi theo ngôn ngữ thiết bị.

```kotlin
// thêm ngôn ngữ: values-fr/strings.xml trong app, cùng tên bb10_*
// hoặc ghi đè từng mục:
Bb10Theme(
    scale = scale,
    strings = rememberBb10Strings().copy(moreActions = "Autres"),
) { ... }
```

Chữ của app (nhãn action, tiêu đề, thông điệp lỗi...) do app tự dịch.

## Trạng thái của ô nhập

```kotlin
Bb10TextField(
    value = email, onValueChange = { email = it },
    label = "Email", required = true,
    error = if (valid) null else "Enter a valid address",   // viền đỏ + thông điệp
    helper = "We never share it",                           // hiện khi không có lỗi
    maxLength = 50,                                         // bộ đếm 12/50
    clearable = true,                                       // nút X
    // enabled = false, readOnly = true, password = true
)
```

## Font

Kit luôn dùng font hệ thống (`FontFamily.Default`) và tôn trọng cỡ chữ hệ thống; không có tùy chọn font riêng.

## Phím

| Phím | Tác dụng |
|---|---|
| D-pad | Di chuyển focus |
| Enter / Center / Space | Kích hoạt phần tử đang focus |
| Menu | Mở/đóng menu bên phải; trên một dòng list có `onLongClick` thì mở context menu |
| Back | Đóng menu / drawer / dialog |
| Phím tắt của action | `Bb10Action(shortcut = Key.C)`, không chạy khi đang gõ trong text field |
| Del | Phím Del vật lý (`Key.Delete` / `Key.Backspace` là một) cho action Delete; gợi ý hiện "Del" |
| Giữ một item (touch) | Menu bên phải thò ra ~2mm và nền tối ngay khi còn giữ, trượt hẳn vào khi thả tay |

Gợi ý phím tắt (chữ cái nhỏ) chỉ hiện trong menu bên phải và chỉ khi máy có bàn phím vật lý.

## Catalog

Module `:catalog` là một app Android liệt kê mọi component với các biến thể và trạng thái, vẽ bằng chính kit (có cả điều khiển cỡ hiển thị, theme tối/sáng, màu accent):

```bash
./gradlew :catalog:installDebug
```

Các trang: Foundations (cỡ hiển thị, màu, chữ), Header (thường, Cancel/OK, Back, tìm kiếm), Action bar (primary + 2, primary + 1, có ☰ và ⋮, không primary), Menus (giữ item để menu thò ra), Tabs, Lists, Forms (mọi trạng thái của ô nhập, checkbox, drop-down, slider, stepper), Pickers (ngày, giờ 24/12 giờ, cột chọn bất kỳ, trong dropdown và popup), Dialogs, Icons, Accessibility.

## Kiểm thử

Test chạy trên JVM (Robolectric + Compose UI test), không cần thiết bị:

```bash
./gradlew :bb10-kotlin-ui-kit:testDebugUnitTest :catalog:testDebugUnitTest
```

- `ScaffoldBehaviourTest`: d-pad từ header xuống item đầu, giữ item để menu peek rồi mở, d-pad đi được trong context menu (kể cả mở bằng cảm ứng), nút ☰ ở bottom bar đẩy app.
- `HighlightTest`: nền highlight là accent tối hơn, nội dung chuyển trắng khi focus và trở lại khi rời đi.
- `TextFieldEditingTest`: d-pad đi qua ô nhập không bị kẹt, Enter / Space / chạm / gõ chữ mới vào ô, Enter ra khỏi ô, dialog nhập.
- `AccessibilityAndLayoutTest`: primary luôn ở giữa (1, 2 action), phím Del kích hoạt Delete, tên cho nút chỉ có icon, heading, trạng thái toggle/selected, vùng chạm 48dp, cỡ hiển thị.
- `PickersAndFlowTest`: picker (d-pad, ngày co theo tháng, 12/24 giờ), picker trong dropdown và popup, chọn nhiều, màn trượt vào giữ focus và trả focus về đúng item.
- `ChromeTest`: bottom bar không action (hai ô ở góc), Back thay cho ☰ (và không dùng được cùng nhau), tab cục bộ / toàn cục, subtitle dưới tiêu đề.
- `ControlsTest`: checkbox, drop-down mở trong dòng và chọn, slider (kéo, semantics, d-pad, nấc), popup hai nút.
- `TextFieldStatesTest`: lỗi / helper / disabled / giới hạn ký tự / nút xóa / read-only, và chuỗi theo ngôn ngữ thiết bị (en, vi).
- `CatalogSmokeTest`: mở lần lượt mọi trang của catalog, không được crash.

## Cấu trúc

```
bb10-kotlin/
  catalog/                       app liệt kê mọi component (module :catalog)
  bb10-kotlin-ui-kit/            module thư viện (namespace duc1607.bb10.designsystem)
    src/main/kotlin/duc1607/bb10/designsystem/
      tokens/                    Bb10Colors, Bb10Typography, Bb10Dimens (Regular + Compact)
      theme/                     Bb10Theme, object Bb10, Bb10Scale (cỡ hiển thị), Bb10Strings
      icons/                     Bb10Icons vẽ bằng code
      components/
        Focus.kt                 bb10Clickable: highlight (accent tối + nội dung trắng), Enter/Space/Menu
        Scaffold.kt              khung màn hình, phím tắt, panel trượt
        ActionBar.kt             Bb10Action, thanh action, menu "⋮" bên phải
        SidePanel.kt             panel trượt trái/phải dùng chung
        Navigation.kt            Bb10NavItem + drawer bên trái
        TitleBar.kt              tiêu đề, Cancel/OK, nút mở drawer
        ListItem.kt              ListItem, LinkItem, Header, Marker
        Menu.kt                  cầu nối giữa list item và menu phải (giữ để peek)
        Tabs.kt                  tab cục bộ và dải tab dùng chung với header
        TextField.kt, Toggle.kt, Checkbox.kt, Dropdown.kt, Slider.kt
        Button.kt, Dialog.kt, ScalePicker.kt, Basics.kt
  DESIGN.md                      quy tắc thiết kế component
```

## Bản quyền

Logo, icon và font của BlackBerry (Slate Pro) không có trong kit này. Đây là dự án cộng đồng, không liên kết với BlackBerry Limited.

## License

[Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)](https://creativecommons.org/licenses/by-nc/4.0/); toàn văn ở file [LICENSE](LICENSE).

- **Được phép:** dùng, sao chép, phân phối lại và sửa đổi kit.
- **Bắt buộc ghi credit:** nêu tên tác giả (duc1607), kèm đường dẫn license, và ghi rõ nếu bạn đã sửa đổi.
- **Không được dùng cho mục đích thương mại.**

Ví dụ ghi credit trong màn hình "Giới thiệu" của app:

> UI kit: BB10 Kotlin UI Kit của duc1607, CC BY-NC 4.0 (https://creativecommons.org/licenses/by-nc/4.0/)

CC khuyến cáo license của họ không được thiết kế riêng cho mã nguồn (không đề cập bằng sáng chế và việc phân phối mã nguồn). Nếu cần một license viết riêng cho phần mềm phi thương mại thì cân nhắc PolyForm Noncommercial 1.0.0.

## Việc nên làm tiếp

- Toast, list có swipe action
- Chỉnh màu/khoảng cách theo ảnh chụp BB10 thật (màu hiện tại là xấp xỉ)
- Thử trên máy thật: chỗ dễ lệch nhất là focus sau khi xóa phần tử đang focus và focus ban đầu của `Bb10Scaffold`
