function sliding_window(input_file, dt, window_size, output_file)
  data = csvread(input_file);
  times = [0:dt:max(data) + 2 *dt];
  events = zeros(1, columns(times));
  i = 1;
  for k = data'
    while k > times(i+1)
      i += 1;
      if i + 1 >= columns(times)
        break;
      endif
    endwhile
    if i + 1 >= columns(times)
      break;
    endif
    events(i) += 1;
  endfor
  i = 1;
  window_arr = [];
  while i + window_size < columns(events)
    window_arr = [window_arr; times(i), sum(events(i:i+window_size))];
    i += 1;
  endwhile
  % save output_file window_arr
  csvwrite(output_file, window_arr);
endfunction
