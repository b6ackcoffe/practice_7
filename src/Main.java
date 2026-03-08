import java.util.*;

// Сохрани как Main.java
public class Main {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("1) COMMAND - УМНЫЙ ДОМ");
        System.out.println("========================================");
        commandDemo();

        System.out.println("\n========================================");
        System.out.println("2) TEMPLATE METHOD - ОТЧЕТЫ");
        System.out.println("========================================");
        templateMethodDemo();

        System.out.println("\n========================================");
        System.out.println("3) MEDIATOR - ЧАТ С КАНАЛАМИ");
        System.out.println("========================================");
        mediatorDemo();
    }

    // =========================================================
    // 1) COMMAND
    // =========================================================

    interface Command {
        void execute();
        void undo();
    }

    static class NoCommand implements Command {
        @Override
        public void execute() {
            System.out.println("Команда для этого слота не назначена.");
        }

        @Override
        public void undo() {
            System.out.println("Нечего отменять.");
        }
    }

    // -------------------- Устройства --------------------

    static class Light {
        private final String place;
        private boolean on;

        public Light(String place) {
            this.place = place;
        }

        public void on() {
            on = true;
            System.out.println(place + ": свет включен");
        }

        public void off() {
            on = false;
            System.out.println(place + ": свет выключен");
        }

        public boolean isOn() {
            return on;
        }
    }

    static class AirConditioner {
        private final String place;
        private boolean on;
        private int temperature = 24;

        public AirConditioner(String place) {
            this.place = place;
        }

        public void on() {
            on = true;
            System.out.println(place + ": кондиционер включен");
        }

        public void off() {
            on = false;
            System.out.println(place + ": кондиционер выключен");
        }

        public void setTemperature(int temperature) {
            this.temperature = temperature;
            System.out.println(place + ": температура кондиционера = " + temperature + "°C");
        }

        public boolean isOn() {
            return on;
        }

        public int getTemperature() {
            return temperature;
        }
    }

    static class TV {
        private final String place;
        private boolean on;
        private int channel = 1;

        public TV(String place) {
            this.place = place;
        }

        public void on() {
            on = true;
            System.out.println(place + ": телевизор включен");
        }

        public void off() {
            on = false;
            System.out.println(place + ": телевизор выключен");
        }

        public void setChannel(int channel) {
            this.channel = channel;
            System.out.println(place + ": телевизор переключен на канал " + channel);
        }

        public boolean isOn() {
            return on;
        }

        public int getChannel() {
            return channel;
        }
    }

    // -------------------- Команды для света --------------------

    static class LightOnCommand implements Command {
        private final Light light;

        public LightOnCommand(Light light) {
            this.light = light;
        }

        @Override
        public void execute() {
            light.on();
        }

        @Override
        public void undo() {
            light.off();
        }
    }

    static class LightOffCommand implements Command {
        private final Light light;

        public LightOffCommand(Light light) {
            this.light = light;
        }

        @Override
        public void execute() {
            light.off();
        }

        @Override
        public void undo() {
            light.on();
        }
    }

    // -------------------- Команды для кондиционера --------------------

    static class AirConditionerOnCommand implements Command {
        private final AirConditioner ac;

        public AirConditionerOnCommand(AirConditioner ac) {
            this.ac = ac;
        }

        @Override
        public void execute() {
            ac.on();
        }

        @Override
        public void undo() {
            ac.off();
        }
    }

    static class AirConditionerOffCommand implements Command {
        private final AirConditioner ac;

        public AirConditionerOffCommand(AirConditioner ac) {
            this.ac = ac;
        }

        @Override
        public void execute() {
            ac.off();
        }

        @Override
        public void undo() {
            ac.on();
        }
    }

    static class SetAirConditionerTemperatureCommand implements Command {
        private final AirConditioner ac;
        private final int newTemperature;
        private int previousTemperature;

        public SetAirConditionerTemperatureCommand(AirConditioner ac, int newTemperature) {
            this.ac = ac;
            this.newTemperature = newTemperature;
        }

        @Override
        public void execute() {
            previousTemperature = ac.getTemperature();
            ac.setTemperature(newTemperature);
        }

        @Override
        public void undo() {
            ac.setTemperature(previousTemperature);
        }
    }

    // -------------------- Команды для телевизора --------------------

    static class TVOnCommand implements Command {
        private final TV tv;

        public TVOnCommand(TV tv) {
            this.tv = tv;
        }

        @Override
        public void execute() {
            tv.on();
        }

        @Override
        public void undo() {
            tv.off();
        }
    }

    static class TVOffCommand implements Command {
        private final TV tv;

        public TVOffCommand(TV tv) {
            this.tv = tv;
        }

        @Override
        public void execute() {
            tv.off();
        }

        @Override
        public void undo() {
            tv.on();
        }
    }

    static class TVSetChannelCommand implements Command {
        private final TV tv;
        private final int newChannel;
        private int previousChannel;

        public TVSetChannelCommand(TV tv, int newChannel) {
            this.tv = tv;
            this.newChannel = newChannel;
        }

        @Override
        public void execute() {
            previousChannel = tv.getChannel();
            tv.setChannel(newChannel);
        }

        @Override
        public void undo() {
            tv.setChannel(previousChannel);
        }
    }

    // -------------------- Макрокоманда --------------------

    static class MacroCommand implements Command {
        private final List<Command> commands;

        public MacroCommand(List<Command> commands) {
            this.commands = commands;
        }

        @Override
        public void execute() {
            for (Command command : commands) {
                command.execute();
            }
        }

        @Override
        public void undo() {
            for (int i = commands.size() - 1; i >= 0; i--) {
                commands.get(i).undo();
            }
        }
    }

    // -------------------- Пульт --------------------

    static class RemoteControl {
        private final Command[] onCommands;
        private final Command[] offCommands;
        private final Deque<Command> undoStack;
        private final Deque<Command> redoStack;

        public RemoteControl(int slots) {
            onCommands = new Command[slots];
            offCommands = new Command[slots];
            undoStack = new ArrayDeque<>();
            redoStack = new ArrayDeque<>();

            Command noCommand = new NoCommand();
            for (int i = 0; i < slots; i++) {
                onCommands[i] = noCommand;
                offCommands[i] = noCommand;
            }
        }

        public void setCommand(int slot, Command onCommand, Command offCommand) {
            if (slot < 0 || slot >= onCommands.length) {
                System.out.println("Ошибка: неправильный номер слота " + slot);
                return;
            }
            onCommands[slot] = onCommand;
            offCommands[slot] = offCommand;
        }

        public void pressOnButton(int slot) {
            if (slot < 0 || slot >= onCommands.length) {
                System.out.println("Ошибка: неправильный номер слота " + slot);
                return;
            }

            Command command = onCommands[slot];
            command.execute();

            if (!(command instanceof NoCommand)) {
                undoStack.push(command);
                redoStack.clear();
            }
        }

        public void pressOffButton(int slot) {
            if (slot < 0 || slot >= offCommands.length) {
                System.out.println("Ошибка: неправильный номер слота " + slot);
                return;
            }

            Command command = offCommands[slot];
            command.execute();

            if (!(command instanceof NoCommand)) {
                undoStack.push(command);
                redoStack.clear();
            }
        }

        public void pressUndoButton() {
            if (undoStack.isEmpty()) {
                System.out.println("Отменять нечего.");
                return;
            }

            Command lastCommand = undoStack.pop();
            lastCommand.undo();
            redoStack.push(lastCommand);
        }

        public void pressRedoButton() {
            if (redoStack.isEmpty()) {
                System.out.println("Повторять нечего.");
                return;
            }

            Command lastUndone = redoStack.pop();
            lastUndone.execute();
            undoStack.push(lastUndone);
        }

        public void printStatus() {
            System.out.println("------ Состояние пульта ------");
            for (int i = 0; i < onCommands.length; i++) {
                System.out.println("Слот " + i + ": ON=" + onCommands[i].getClass().getSimpleName()
                        + ", OFF=" + offCommands[i].getClass().getSimpleName());
            }
        }
    }

    static void commandDemo() {
        Light livingRoomLight = new Light("Гостиная");
        AirConditioner bedroomAc = new AirConditioner("Спальня");
        TV kitchenTv = new TV("Кухня");

        Command livingLightOn = new LightOnCommand(livingRoomLight);
        Command livingLightOff = new LightOffCommand(livingRoomLight);

        Command acOn = new AirConditionerOnCommand(bedroomAc);
        Command acOff = new AirConditionerOffCommand(bedroomAc);
        Command acSet18 = new SetAirConditionerTemperatureCommand(bedroomAc, 18);

        Command tvOn = new TVOnCommand(kitchenTv);
        Command tvOff = new TVOffCommand(kitchenTv);
        Command tvSet5 = new TVSetChannelCommand(kitchenTv, 5);

        RemoteControl remote = new RemoteControl(5);

        remote.setCommand(0, livingLightOn, livingLightOff);
        remote.setCommand(1, acOn, acOff);
        remote.setCommand(2, tvOn, tvOff);

        remote.printStatus();

        System.out.println("\nНажимаем кнопки:");
        remote.pressOnButton(0);
        remote.pressOnButton(1);
        acSet18.execute();
        remote.pressOnButton(2);
        tvSet5.execute();

        System.out.println("\nОтмена:");
        remote.pressUndoButton();
        remote.pressUndoButton();

        System.out.println("\nПовтор:");
        remote.pressRedoButton();

        System.out.println("\nМакрокоманда 'Я ушел из дома':");
        Command awayMode = new MacroCommand(Arrays.asList(
                new LightOffCommand(livingRoomLight),
                new AirConditionerOffCommand(bedroomAc),
                new TVOffCommand(kitchenTv)
        ));
        awayMode.execute();

        System.out.println("\nОтмена макрокоманды:");
        awayMode.undo();

        System.out.println("\nПустой слот:");
        remote.pressOnButton(4);
    }

    // =========================================================
    // 2) TEMPLATE METHOD
    // =========================================================

    static abstract class ReportGenerator {

        public final void generateReport() {
            log("Старт генерации отчета");
            loadData();
            formatData();
            generateHeader();
            generateBody();
            generateFooter();

            if (customerWantsSave()) {
                saveReport();
            } else {
                sendByEmail();
            }

            log("Генерация отчета завершена");
            System.out.println();
        }

        protected void loadData() {
            System.out.println("Загрузка данных для отчета...");
        }

        protected abstract void formatData();

        protected abstract void generateHeader();

        protected abstract void generateBody();

        protected void generateFooter() {
            System.out.println("Добавление стандартного footer...");
        }

        protected boolean customerWantsSave() {
            return true;
        }

        protected void saveReport() {
            System.out.println("Сохранение отчета в файл...");
        }

        protected void sendByEmail() {
            System.out.println("Отправка отчета по email...");
        }

        protected void log(String message) {
            System.out.println("[LOG] " + message);
        }
    }

    static class PdfReport extends ReportGenerator {
        @Override
        protected void formatData() {
            System.out.println("Форматирование данных для PDF...");
        }

        @Override
        protected void generateHeader() {
            System.out.println("Создание PDF-заголовка...");
        }

        @Override
        protected void generateBody() {
            System.out.println("Создание PDF-содержимого...");
        }

        @Override
        protected void saveReport() {
            System.out.println("PDF отчет сохранен как file.pdf");
        }
    }

    static class ExcelReport extends ReportGenerator {
        @Override
        protected void formatData() {
            System.out.println("Форматирование данных для Excel...");
        }

        @Override
        protected void generateHeader() {
            System.out.println("Создание Excel-заголовка...");
        }

        @Override
        protected void generateBody() {
            System.out.println("Создание Excel-таблицы...");
        }

        @Override
        protected void saveReport() {
            System.out.println("Excel отчет сохранен как file.xlsx");
        }
    }

    static class HtmlReport extends ReportGenerator {
        @Override
        protected void formatData() {
            System.out.println("Форматирование данных для HTML...");
        }

        @Override
        protected void generateHeader() {
            System.out.println("Создание HTML-заголовка...");
        }

        @Override
        protected void generateBody() {
            System.out.println("Создание HTML-страницы...");
        }

        @Override
        protected void generateFooter() {
            System.out.println("Добавление HTML footer с тегами </body></html>...");
        }

        @Override
        protected boolean customerWantsSave() {
            return false;
        }

        @Override
        protected void sendByEmail() {
            System.out.println("HTML отчет отправлен по email.");
        }
    }

    static class CsvReport extends ReportGenerator {
        @Override
        protected void formatData() {
            System.out.println("Форматирование данных в CSV-строки...");
        }

        @Override
        protected void generateHeader() {
            System.out.println("Создание CSV-заголовка: name,amount,date");
        }

        @Override
        protected void generateBody() {
            System.out.println("Создание CSV-строк с данными...");
        }

        @Override
        protected void saveReport() {
            System.out.println("CSV отчет сохранен как file.csv");
        }
    }

    static void templateMethodDemo() {
        ReportGenerator pdf = new PdfReport();
        ReportGenerator excel = new ExcelReport();
        ReportGenerator html = new HtmlReport();
        ReportGenerator csv = new CsvReport();

        System.out.println("PDF отчет:");
        pdf.generateReport();

        System.out.println("Excel отчет:");
        excel.generateReport();

        System.out.println("HTML отчет:");
        html.generateReport();

        System.out.println("CSV отчет:");
        csv.generateReport();
    }

    // =========================================================
    // 3) MEDIATOR
    // =========================================================

    interface ChatMediator {
        void addUser(String channelName, ChatUser user);
        void removeUser(String channelName, ChatUser user);
        void sendMessage(String channelName, String message, ChatUser sender);
        void sendPrivateMessage(ChatUser from, ChatUser to, String message);
    }

    static class ChannelMediator implements ChatMediator {
        private final Map<String, List<ChatUser>> channels = new HashMap<>();

        @Override
        public void addUser(String channelName, ChatUser user) {
            channels.putIfAbsent(channelName, new ArrayList<>());
            List<ChatUser> users = channels.get(channelName);

            if (users.contains(user)) {
                System.out.println(user.getName() + " уже находится в канале " + channelName);
                return;
            }

            users.add(user);
            user.setMediator(this);
            user.setChannel(channelName);

            notifyChannel(channelName, "[СИСТЕМА] " + user.getName() + " подключился к каналу.");
        }

        @Override
        public void removeUser(String channelName, ChatUser user) {
            List<ChatUser> users = channels.get(channelName);

            if (users == null || !users.contains(user)) {
                System.out.println("Ошибка: пользователь " + user.getName()
                        + " не найден в канале " + channelName);
                return;
            }

            users.remove(user);
            notifyChannel(channelName, "[СИСТЕМА] " + user.getName() + " покинул канал.");
        }

        @Override
        public void sendMessage(String channelName, String message, ChatUser sender) {
            List<ChatUser> users = channels.get(channelName);

            if (users == null) {
                System.out.println("Ошибка: канала " + channelName + " не существует.");
                return;
            }

            if (!users.contains(sender)) {
                System.out.println("Ошибка: пользователь " + sender.getName()
                        + " не состоит в канале " + channelName);
                return;
            }

            for (ChatUser user : users) {
                if (user != sender) {
                    user.receive("[" + channelName + "] " + sender.getName() + ": " + message);
                }
            }
        }

        @Override
        public void sendPrivateMessage(ChatUser from, ChatUser to, String message) {
            if (from == null || to == null) {
                System.out.println("Ошибка: неверные данные для личного сообщения.");
                return;
            }

            to.receive("[ЛИЧНОЕ] " + from.getName() + ": " + message);
        }

        private void notifyChannel(String channelName, String systemMessage) {
            List<ChatUser> users = channels.get(channelName);
            if (users == null) {
                return;
            }

            for (ChatUser user : users) {
                user.receive("[" + channelName + "] " + systemMessage);
            }
        }
    }

    interface ChatUser {
        void send(String message);
        void sendPrivate(ChatUser to, String message);
        void receive(String message);
        String getName();
        void setMediator(ChatMediator mediator);
        void setChannel(String channelName);
        String getChannel();
    }

    static class User implements ChatUser {
        private final String name;
        private ChatMediator mediator;
        private String channel;

        public User(String name) {
            this.name = name;
        }

        @Override
        public void send(String message) {
            if (mediator == null || channel == null) {
                System.out.println("Ошибка: " + name + " не подключен к каналу.");
                return;
            }
            mediator.sendMessage(channel, message, this);
        }

        @Override
        public void sendPrivate(ChatUser to, String message) {
            if (mediator == null) {
                System.out.println("Ошибка: " + name + " не подключен к посреднику.");
                return;
            }
            mediator.sendPrivateMessage(this, to, message);
        }

        @Override
        public void receive(String message) {
            System.out.println(name + " получил сообщение -> " + message);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setMediator(ChatMediator mediator) {
            this.mediator = mediator;
        }

        @Override
        public void setChannel(String channelName) {
            this.channel = channelName;
        }

        @Override
        public String getChannel() {
            return channel;
        }
    }

    static void mediatorDemo() {
        ChannelMediator mediator = new ChannelMediator();

        ChatUser alice = new User("Alice");
        ChatUser bob = new User("Bob");
        ChatUser charlie = new User("Charlie");
        ChatUser david = new User("David");

        mediator.addUser("general", alice);
        mediator.addUser("general", bob);
        mediator.addUser("general", charlie);

        mediator.addUser("games", david);
        mediator.addUser("games", bob);

        System.out.println("\nСообщения в канале general:");
        alice.send("Всем привет!");
        bob.send("Привет, Alice!");

        System.out.println("\nЛичное сообщение:");
        charlie.sendPrivate(alice, "Alice, потом скину файл.");

        System.out.println("\nСообщения в канале games:");
        david.send("Кто сегодня играет?");
        bob.send("Я вечером смогу.");

        System.out.println("\nУдаление пользователя:");
        mediator.removeUser("general", bob);

        System.out.println("\nПроверка после удаления:");
        bob.send("Я еще здесь?");

        System.out.println("\nОшибка с несуществующим каналом:");
        ChatUser eva = new User("Eva");
        eva.send("Сообщение без канала");
    }
}