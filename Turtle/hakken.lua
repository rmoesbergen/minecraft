goingLeft = true

function plantSapling()
  local place = turtle.getItemCount(1) > 1
  turtle.forward()
  turtle.suck()
  turtle.turnLeft()
  turtle.suck()
  turtle.turnLeft()
  turtle.suck()
  if place then
    print("placing sapling")
    turtle.place()
  end
  turtle.turnLeft()
  turtle.suck()
  turtle.turnLeft()
  turtle.suck()
end

function turnAround(left)
  if left then
    turtle.turnLeft()
    turtle.forward()
    turtle.turnLeft()
    turtle.forward()
  else
    turtle.turnRight()
    turtle.forward()
    turtle.turnRight()
    turtle.forward()
  end
  local ok, data = turtle.inspectDown()
  if data.name ~= 'minecraft:cobblestone' then
    -- end of the line, reverse
    if left then
      turtle.turnLeft()
      turtle.forward()
      turtle.turnRight()
    else
      turtle.turnRight()
      turtle.forward()
      turtle.turnLeft()
    end
    goingLeft = not goingLeft
  end  
end

function chopTree()
  print("chopping tree")
  goDown()
  turtle.dig()
  turtle.forward()
  turtle.suck()
  while turtle.detectUp() do
    turtle.digUp()
    turtle.up()
  end
  goDown()
end


function goDown()
  while not turtle.detectDown() do
    turtle.suckDown()
    turtle.down()
  end
end

function dumpInventory()
  print("dumping inventory")
  for slot=2, 16, 1 do
    turtle.select(slot)
    turtle.drop()
  end
  turtle.select(1)
end


function movement()
  turtle.suck()

  local ok, data = turtle.inspectDown()
  if data.name == "minecraft:dirt" then
    plantSapling()
  else
    print(data.name)
    if data.name == 'minecraft:cobblestone' then
      if not turtle.forward() then
        -- blocked, is het een boom?
        local ok, data = turtle.inspect()
        print("blocked by "..data.name)
        if data.name == 'minecraft:log' then
          -- boom, kappen
          chopTree()
        else
          if data.name == 'yabba:item_barrel' then
            -- chest, dump zooi
            dumpInventory()
          end          
          -- geen boom, ga er overheen
          turtle.up()
          turtle.forward()
          turtle.forward()
          goDown()
        end
      end
    else
      -- turn, water ofzo
      turnAround(goingLeft)
      goingLeft = not goingLeft
    end
  end
end

goDown()
while true do
  movement()
end
