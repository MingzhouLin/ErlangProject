-module(calling).
-export([call/2, frends/1]).

call(Users, Master)->
	Address_List=registry(Users,[], Master),
	% io:format("~w~n",[Address_List]),
	send_message(Users, Address_List, Master).

%%% registry process
registry([],User_Address_List, Master)->
	User_Address_List;
registry([First|Rest], User_Address_List, Master)->
	registry(Rest, start_process(First, User_Address_List, Master), Master).
start_process({Caller, Replyer}, User_Address_List, Master)->
	[{Caller, spawn(calling, frends, [Master])}|User_Address_List].
stop_process([], Master)->
	Master ! finished,
	io:format("");
stop_process([First|Rest], Master)->
	stop(First),
	stop_process(Rest, Master).

stop({Name, Address})->
	Address ! {Name}.
%%% message sending process
send_message([], Address_List, Master)->
	timer:sleep(1000),
	stop_process(Address_List, Master);
send_message([First|Rest], Address_List, Master)->
	% io:format("~w~n", [First]),
	send_and_reply(First, Address_List),
	send_message(Rest, Address_List, Master).

send_and_reply({Caller, Replyer}, Address_List)->
	Caller_Address=get_address(lists:keysearch(Caller, 1, Address_List)),
	% io:format("~w~n", [Caller_Address]),
	for_each_replyer_send_message(Replyer, Caller,Caller_Address, Address_List).

for_each_replyer_send_message([], Caller, Caller_Address, Address_List)->
	io:format("");
for_each_replyer_send_message([First|Rest], Caller, Caller_Address, Address_List)->
	% io:format("~w~n", [lists:keysearch(First, 1, Address_List)]),
	get_address(lists:keysearch(First, 1, Address_List)) ! {Caller, First, Caller_Address, intro},
	for_each_replyer_send_message(Rest, Caller, Caller_Address, Address_List).

frends(Master)->
	receive
		{Name}->
			io:format("Process ~w has received no calls for 1 second, ending...~n", [Name]);
		{Caller, First, Caller_Address, Type}->
			timer:sleep(random:uniform(100)),
			{A1, A2, A3}=now(),
			Master ! {Caller, First, Type, A3},
			Caller_Address ! {First, Caller, A3};
		{Caller, First, Milliseconds}->
			Master ! {Caller, First, reply, Milliseconds}
	end,
	frends(Master).

get_address({value, {Name, Address}})->
	Address.