import {AppBar, Box, Button, Link, Stack} from "@mui/material";
import {useState} from "react";

interface NavbarProps {
  items: { [key: string]: string[] };
}

function Navbar({items}: NavbarProps) {
  const [selectedGroup, setSelectedGroup] = useState<string>('')

  const scrollToSubitem = (id: string) => {
    const element = document.getElementById(id);
    if (element) {
      element.scrollIntoView({behavior: 'smooth'});
    }
  };

  return (
    <>
      <AppBar>
        <Stack direction='row' justifyContent='space-around'>
          {Object.keys(items).map((item, index) => (
            <Box key={index}>
              <Button
                variant='contained'
                onClick={() => setSelectedGroup(item)}
              >{item}</Button>
            </Box>
          ))}
        </Stack>
        <Stack direction='row' justifyContent='space-around'>
          {selectedGroup && items[selectedGroup].map((subItem, index) => (
            <Box key={index}>
              <Link
                href={`#${subItem}`}
                underline='none'
                color='inherit'
                onClick={(e) => {
                  e.preventDefault();
                  scrollToSubitem(subItem);
                }}
              >{subItem}</Link>
            </Box>
          ))}
        </Stack>
      </AppBar>
    </>
  );
}

export default Navbar;