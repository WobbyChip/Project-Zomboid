local old_fn = ISReadABook.new;

function ISReadABook:new(...)
    local o = old_fn(self, ...);
    if o.maxTime ~= 1 then
        o.maxTime = o.maxTime * 0.25;
    end
    return o;
end