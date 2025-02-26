package com.taskapp.logic;

import java.time.LocalDate;
import java.util.List;

import com.taskapp.dataaccess.LogDataAccess;
import com.taskapp.dataaccess.TaskDataAccess;
import com.taskapp.dataaccess.UserDataAccess;
import com.taskapp.exception.AppException;
import com.taskapp.model.Log;
import com.taskapp.model.Task;
import com.taskapp.model.User;

public class TaskLogic {
    private final TaskDataAccess taskDataAccess;
    private final LogDataAccess logDataAccess;
    private final UserDataAccess userDataAccess;


    public TaskLogic() {
        taskDataAccess = new TaskDataAccess();
        logDataAccess = new LogDataAccess();
        userDataAccess = new UserDataAccess();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param taskDataAccess
     * @param logDataAccess
     * @param userDataAccess
     */
    public TaskLogic(TaskDataAccess taskDataAccess, LogDataAccess logDataAccess, UserDataAccess userDataAccess) {
        this.taskDataAccess = taskDataAccess;
        this.logDataAccess = logDataAccess;
        this.userDataAccess = userDataAccess;
    }

    /**
     * 全てのタスクを表示します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findAll()
     * @param loginUser ログインユーザー
     */
    public void showAll(User loginUser) {
        //findAllメソッドを実行して、データの一覧を取得
        List<Task> tasks = taskDataAccess.findAll();

        //取得したデータを表示する
        tasks.forEach(task -> {
            String status = "";
            switch (task.getStatus()) {
                case 1:
                    status += "着手中";
                    break;
                case 2:
                    status += "完了";
                    break;
                default:
                    status += "未着手";
                    break;
            }
            String repUser = "";
            if(task.getRepUser().getCode() == loginUser.getCode()){
                repUser += "あなたが担当しています";
            }else{
                repUser += task.getRepUser().getName() + "が担当しています";
            }

            //一覧表を追加
            System.out.println("タスク名："+task.getName() + ", 担当者名：" + repUser + ", ステータス：" + status);
        });
    }

    /**
     * 新しいタスクを保存します。
     *
     * @see com.taskapp.dataaccess.UserDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#save(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code タスクコード
     * @param name タスク名
     * @param repUserCode 担当ユーザーコード
     * @param loginUser ログインユーザー
     * @throws AppException ユーザーコードが存在しない場合にスローされます
     */
    public void save(int code, String name, int repUserCode,
                    User loginUser) throws AppException {
        if(userDataAccess.findByCode(repUserCode) == null){
            throw new AppException("存在するユーザーコードを入力してください");
        }
            //選択したtaskデータを取得する
           User user =  userDataAccess.findByCode(repUserCode);
            Task task = new Task(code, name, 0, user);
            taskDataAccess.save(task);
            LocalDate date = LocalDate.now();
            Log log = new Log(code, loginUser.getCode(), 0, date);
            logDataAccess.save(log);
    }

    /**
     * タスクのステータスを変更します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#update(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code タスクコード
     * @param status 新しいステータス
     * @param loginUser ログインユーザー
     * @throws AppException タスクコードが存在しない、またはステータスが前のステータスより1つ先でない場合にスローされます
     */
    public void changeStatus(int code, int status,
                            User loginUser) throws AppException {
                Task task = taskDataAccess.findByCode(code);
            if(task == null){
                throw new AppException("存在するタスクコードを入力してください。");
            }
            if(status - task.getStatus() != 1){
                throw new AppException("ステータスは、前のステータスより1つ先のもののみを選択してください");
            }
            task.setStatus(status);
            taskDataAccess.update(task);
            LocalDate date = LocalDate.now();
            Log log = new Log(code, loginUser.getCode(), task.getStatus(), date);
            logDataAccess.save(log);
    }

    /**
     * タスクを削除します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#delete(int)
     * @see com.taskapp.dataaccess.LogDataAccess#deleteByTaskCode(int)
     * @param code タスクコード
     * @throws AppException タスクコードが存在しない、またはタスクのステータスが完了でない場合にスローされます
     */
    // public void delete(int code) throws AppException {
    // }
}