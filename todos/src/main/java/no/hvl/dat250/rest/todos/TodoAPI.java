package no.hvl.dat250.rest.todos;

import com.google.gson.Gson;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Optional;

import static spark.Spark.*;

/**
 * Rest-Endpoint.
 */
public class TodoAPI {
    static ArrayList<Todo> todos = new ArrayList<>();
    static long idCounter = 0;

    public static void main(String[] args) {
        if (args.length > 0) {
            port(Integer.parseInt(args[0]));
        } else {
            port(8080);
        }

        after((req, res) -> res.type("application/json"));

        // TODO: Implement API, such that the testcases succeed.

        get("/todos", (req, res) -> {
            Gson gson = new Gson();
            return gson.toJson(todos);
        });

        get("/todos/:id", (req, res) -> {
            Long idToFind;
            try {
                idToFind = Long.parseLong(req.params(":id"));
            } catch (NumberFormatException ex) {
                return String.format("The id \"%s\" is not a number!", req.params(":id"));
            }
            Optional<Todo> foundTodo = todos.stream().filter(todo -> todo.getId().equals(idToFind)).findFirst();
            if(foundTodo.isPresent()){
                return foundTodo.get().toJson();
            }else {
                return String.format("Todo with the id  \"%s\" not found!", idToFind);
            }

        });

        post("/todos", (req,res) -> {

            Gson gson = new Gson();

            Todo newTodo = gson.fromJson(req.body(), Todo.class);
            Todo fixedTodo = new Todo(idCounter,newTodo.getSummary(), newTodo.getDescription());

            todos.add(fixedTodo);

            idCounter++;

            return fixedTodo.toJson();

        });

        put("/todos/:id", (req,res) -> {

            Gson gson = new Gson();

            Todo updatedTodo = gson.fromJson(req.body(), Todo.class);
            Long idToFind;
            try {
                idToFind = Long.parseLong(req.params(":id"));
            } catch (NumberFormatException ex) {
                return String.format("The id \"%s\" is not a number!", req.params(":id"));
            }


            Optional<Todo> foundTodo = todos.stream().filter(todo -> todo.getId().equals(idToFind)).findFirst();
            if(foundTodo.isPresent()){
                if(idToFind.equals(updatedTodo.getId())) {
                    todos.remove(foundTodo.get());
                    todos.add(updatedTodo);
                    return updatedTodo;
                } else {
                    return "ID and todo id don't match";
                }
            }else {
                return String.format("Todo with the id  \"%s\" not found!", idToFind);
            }

        });

        delete("/todos/:id", (req,res) -> {



            Long idToFind;
            try {
                idToFind = Long.parseLong(req.params(":id"));
            } catch (NumberFormatException ex) {
                return String.format("The id \"%s\" is not a number!", req.params(":id"));
            }


            Optional<Todo> foundTodo = todos.stream().filter(todo -> todo.getId().equals(idToFind)).findFirst();
            if(foundTodo.isPresent()){
                todos.remove(foundTodo.get());
                return String.format("Todo with the id  \"%s\" was deleted", idToFind);
            }else {
                return String.format("Todo with the id  \"%s\" not found!", idToFind);
            }

        });
    }
}
