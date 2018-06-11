-module(exchange).
-export([run/0, master/0]).
run()->
	main(file:consult("calls.txt")).


main({ok,File})->
	print_first_line(),
	print_file(File),
	calling:call(File, start()).


print_file([])->
	io:format("~n");
print_file([First|Rest])->
	print_temp(First),
	print_file(Rest).

print_temp({Caller, Callee}) ->
    io:format("~w : ~w ~n", [Caller, Callee]).

print_first_line()->
	io:format("** Calls to be made ** ~n").

start() ->
    spawn(exchange, master,[]).

master() ->
    receive
        finished ->
            io:format("Master has received no replies for 1.5 seconds, ending...~n");
        {Caller, Replyer, Type, Milliseconds} ->
            io:format("~w received ~w message from ~w [~w]~n", [Replyer, Type, Caller, Milliseconds])
    end,
    master().