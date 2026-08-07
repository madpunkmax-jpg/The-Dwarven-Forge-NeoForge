# Прогрес порту: The-Dwarven-Forge (Forge 1.20.1) → The_Dwarven_Neoforge (NeoForge 1.21.1)

Останнє оновлення: 2026-07-28

## Розташування

- Джерело (Forge 1.20.1, не чіпати): `C:\Projects\The-Dwarven-Forge\The-Dwarven-Forge`
- Ціль (NeoForge 1.21.1, тут ведеться робота): `C:\Projects\The-Dwarven-Forge\The_Dwarven_Neoforge`
- Шаблон, з якого бутстрапили ціль: гілка `origin/archive/1.21` репозиторію `neoforged/MDK`, склонованого в `C:\Projects\The-Dwarven-Forge\NeoForge 1.21.1 MDK\neoforge-1.21.1-mdk` (саме там `minecraft_version=1.21.1` точно збігається; гілка `main` цього репозиторія більше не містить робочого шаблону).
- JDK 21: `C:\Users\madpu\AppData\Local\Programs\Eclipse Adoptium\jdk-21.0.11.10-hotspot` (вже прописаний як `JAVA_HOME` в оточенні — `gradlew` працює без додаткових налаштувань).

## Статус кроків плану

1. ✅ Бутстрап проєкту з MDK-шаблону (`gradle.properties`, перейменування пакету `com.example.examplemod` → `dwarven_forge`, класи `DwarvenForgeMain`/`DwarvenForgeConfig`).
2. ✅ `build.gradle` / `gradle.properties` / `neoforge.mods.toml` — репозиторії й залежності GeckoLib/JEI/Health Indicator, без mixin-плагіна (NeoForge має вбудовану підтримку Mixin).
3. ✅ Реєстри: `DwarvenForge_Registries`, `DwarvenForge_Items`, `DwarvenForge_ItemsMaterials` (ArmorMaterial+Tier), `DwarvenForge_Sounds`, `DwarvenForge_Serializers`, `DwarvenForge_Schedules`, `DwarvenForge_EntityAttributes`, `RuneItem`, `DwarvenArmorItem`, `RuneSmithingRecipe`.
4. ✅ Entities/AI: `DwarfEntity`, увесь пакет `ai/*` (`DwarfGoalPackages`, `DwarfTrades`, `AcquireDwarfPoi`, `StartAttackingDwarf`, `ValidateNearbyDwarfPoi`), рендер/модель дворфа, `DwarvenForge_Entities`, `DWARF_SPAWN_EGG`.
5. ✅ Events: `DwarvenForgeMain` (mod-bus) + новий `DwarvenForgeEvents` (game-bus, окремий клас — обов'язково, інакше NeoForge падає при завантаженні).
6. ✅ Mixins (5 з 6 — `ArmorItemMixin` прибрано за непотрібністю, див. нижче): `LivingEntityMixin`, `ItemStackMixin`, `GrindstoneMenuMixin`, `ZombieMixin`, `VillagerHostilesSensorMixin`.
7. ✅ Datagen: `DwarvenForge_DataGenerator`, `RuneSmithingRecipeBuilder`.
8. ✅ Resources: assets (текстури/анімації/geo/lang/sounds), worldgen (structure/structure_set/template_pool/nbt), `pack.mcmeta` (pack_format 48).

## ЩО ЗАЛИШИЛОСЬ (з цього продовжити після `/clear`)

**2026-08-07: два реальні баги знайдені й виправлені під час ручного тестування в `runClient` (я запускала клієнт для користувача, він грав; я діагностувала через headless-сервер+RCON паралельно). Golden path (крафт, руни/кузня/grindstone, дворф-воїн) підтверджений користувачем як робочий.**

### Баг 1 (виправлено): краш при спробі торгівлі з дворфом-воїном

`DwarfEntity.mobInteract()` викликав `this.getOffers()` без перевірки `!this.level().isClientSide` — ванільний `AbstractVillager.getOffers()` навмисно кидає `IllegalStateException`, якщо викликати його на клієнті (офери мають надходити з сервера через пакет). У ванільному `Villager.mobInteract` весь цей блок обгорнутий у `if (!this.level().isClientSide)`, у порту цю обгортку загубили. Виправлено в [DwarfEntity.java:521](../The_Dwarven_Neoforge/src/main/java/dwarven_forge/entity/DwarfEntity.java) — весь блок з `getOffers()`/`startTrading()` тепер всередині `if (!this.level().isClientSide)`, як в оригіналі. Crash report для довідки: `runs/client/crash-reports/crash-2026-08-07_14.24.30-client.txt`.

### Баг 2 (виправлено): dwarven_village взагалі не генерувалась (в жодному світі/сіді)

Симптом: `/locate structure dwarven_forge:dwarven_village` знаходив координати, але на місці — чистий природний рельєф, жодного блока структури, навіть під землею.

Діагностика (headless `runServer` на **копії** реального сейву користувача — `runs/client/saves/<світ>` скопійовано в `runs/server/world`, оригінал не займала):
1. Написала мінімальний Python NBT/Anvil-парсер (без залежностей, `scratchpad/nbt_reader.py` + `scratchpad/scan_structures.py`), щоб читати `structures/starts` прямо з `.mca`-файлів регіону — обійшла потребу гадати, довірилась сирим даним світу.
2. Знайшла: bounding box зареєстрованого structure-start — **2×2×2** блоки замість заявлених у NBT **9×8×9** (`dwarf_fountain_1.nbt`, 648 блоків). RCON-проби блоків (`execute if block`) підтвердили: на місці — суцільний природний камінь/граніт/вода, жодного `stone_bricks`/`lantern`/`bell`.
3. Порівняла з ванільним `village_plains` в тому ж світі — воно згенерувалось ідеально (121 частина, правильні розміри). Отже проблема не в NeoForge/оточенні, а специфічна для мода.
4. Простежила код (`StructureTemplateManager.getOrCreate`): якщо ресурс не знайдено — тихо (без винятку!) повертається порожній `new StructureTemplate()` (розмір 0×0×0). Bounding-box математика для розміру 0 якраз дає inclusive-діапазон -1..0 = 2 блоки — точний збіг із симптомом.
5. **Корінь причини:** з 1.21 (pack_format 48) Mojang перейменували датапак-папку структурних NBT-шаблонів з **`data/<ns>/structures/`** (множина, стара конвенція) на **`data/<ns>/structure/`** (однина). Підтвердила прямо в ванільному `client-extra.jar`: `data/minecraft/structure/village/plains/...`. У мода всі 44 `.nbt`-файли лежали за старим шляхом `data/dwarven_forge/structures/...` — незмінна копія з Forge 1.20.1,worldgen JSON-и (`worldgen/structure/*.json`) від цього не постраждали (інша папка), тому `/locate` і реєстрація jigsaw-старту працювали, а самі NBT-шаблони — ні.
6. **Фікс:** перейменувала `src/main/resources/data/dwarven_forge/structures/` → `.../structure/` (однина). Ніяких посилань на стару назву в Java-коді не було.
7. **Верифікація:** після фіксу — свіжий forceload дав bounding box **[9008,20,8784]→[9016,27,8792] = 9×8×9**, **64 children-частини** (будинки/вулиці/дворф-воїн), замість 1 деградованої заглушки. Підтверджено RCON-пробами (`polished_andesite` — не природний блок — знайдений на місці).

**Важливо для користувача:** вже згенеровані чанки в старих сейвах (де dwarven_village вже "стартувала" зі старим багом) залишаться зіпсованими назавжди — Minecraft не перегенеровує чанки. Потрібно шукати нові, ще не відвідані координати (новий `/locate` в непроглянутій області) або новий світ.

### Залишилось

GUI-golden-path (крафт/руни/кузня/grindstone/тултіпи/дворф) користувач вже підтвердив вручну як робочий, окрім двох багів вище — обидва виправлені. Варто ще раз пройти повний цикл в `runClient` після цих правок (особливо: торгівля з дворфом-воїном більше не мала б крашити; нове поселення в непройденій області мало б з'явитися).

Якщо після `runClient` щось впаде — найімовірніше це буде щось у геометрії/анімаціях GeckoLib (`.geo.json`/`.animation.json`), бо це єдиний ресурс, який я досі не звіряла проти реального декомпільованого коду 1.21.1 (worldgen тепер перевірено автоматично, звіряла лише формат реєстрації матеріалів/рецептів/тегів через Java API).

## Методологія, яку варто продовжувати

Протягом усього порту НЕ покладались на саму документацію чи здогадки API — кожен спірний момент перевірявся:
1. Компіляцією (`./gradlew compileJava` з директорії `The_Dwarven_Neoforge`, фонові Bash-виклики, бо перший запуск на нову версію NeoForge перекомпільовує ванільний Minecraft і займає кілька хвилин).
2. Реальним запуском (`./gradlew runData`) — це ловить помилки, яких компілятор не бачить (невалідні mixin-таргети, конфлікти реєстрації подій, порядок лінивого завантаження класів).
3. Коли потрібно було точно знати сигнатуру/API 1.21.1 — не гадали і не вірили застарілим статтям, а діставали правду з локального кешу Gradle:
   - декомпільовані джерела ванільного Minecraft: `C:\Users\madpu\.gradle\caches\neoformruntime\intermediate_results\sourcesAndCompiledWithNeoForge_*_output.jar` (шукати нужний файл: `unzip -l "$JAR" | grep -i ІмяКласу.java`, розпакувати й прочитати).
   - `javap -p` на реальних `.class`-файлах з `C:\Users\madpu\.gradle\caches\modules-2\...` (NeoForge universal jar, GeckoLib jar тощо) — коли треба лиш сигнатура, без повного джерела.

Цей самий підхід треба застосувати і для `runClient`, і якщо десь ще щось зламається.

## Ключові несподіванки/рішення (для контексту, не для повторної роботи)

- GeckoLib 4.9.2 прибрав сегмент `core` з пакетів (`geckolib.core.animation.*` → `geckolib.animation.*`), `CoreGeoBone` злили в `GeoBone`, `DynamicGeoEntityRenderer` переїхав у `renderer.specialty`.
- `ArmorMaterial`/`Tier` стали registry-об'єктами; durability броні тепер задається explicit на кожному предметі, а не полем матеріалу.
- Руни: NBT-тег `"Runes"` тепер живе в `DataComponents.CUSTOM_DATA`/`CustomData`, структура самого тегу не змінилась.
- `ArmorItemMixin` видалили повністю — замінили на override `getDefaultAttributeModifiers()` прямо в `DwarvenArmorItem` (простіше і без мixin'а).
- `LivingHurtEvent` видалили з NeoForge — замінник `LivingDamageEvent.Pre`.
- `ToolActions` → `ItemAbilities`; `ForgeHooks`→`CommonHooks`; `ForgeEventFactory`→`EventHooks`; `ForgeRegistries.VILLAGER_PROFESSIONS`→`BuiltInRegistries.VILLAGER_PROFESSION`.
- `MerchantOffer` тепер бере `ItemCost` замість `ItemStack` для вартості; `EnchantmentHelper.enchantItem` вимагає `RegistryAccess`+тег зачарувань; `SuspiciousStewItem.saveMobEffect` прибрали (тепер компонент `SUSPICIOUS_STEW_EFFECTS`); `DyeableLeatherItem`/`DyeableArmorItem` прибрали (тепер `ItemTags.DYEABLE`+`DyedItemColor`).
- Рецепти: `FinishedRecipe` видалили, тепер `RecipeOutput` (і зручний `recipeOutput.advancement()`); `forge:armors`/`ItemTags.TOOLS` → уніфіковані з Fabric теги під неймспейсом `c:` (`Tags.Items.ARMORS`, `Tags.Items.TOOLS`).
- `EntityAttributeCreationEvent` — мод-бас-only подія; НЕ можна змішувати в одному класі зі static-scan для game-bus (звідси розділення `DwarvenForgeMain`/`DwarvenForgeEvents`).
- `DwarvenForge_ItemsMaterials.DWARVEN_ARMOR_MATERIAL` реєструється лише при завантаженні класу — треба примусово "торкнутись" класу (`DwarvenForge_ItemsMaterials.touch()`) в конструкторі мода ДО того, як `RegisterEvent` для `ArmorMaterial` встигне відпрацювати.
- `neo_version` підняли з 21.1.176 до 21.1.244, бо найновіший JEI вимагав ≥21.1.238.
