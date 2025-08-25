import {useEffect, useState} from "react";
import {useHttpStream} from "./hooks/useHttpStreamReturn.ts";
import Navbar from "./components/Navbar.tsx";


type Image = {
  image: string;
  index: number;
  itemGroup: ItemGroup;
}

type ItemGroup = {
  id: number;
  items: number[];
  groupName: string;
  superGroup: "food" | "drinks";
}

function AppTest() {
  const {data: images, startStream} = useHttpStream<Image>(`${import.meta.env.VITE_APP_PATH}/images`)
  const [navItems, setNavItems] = useState<{ [key: string]: string[] }>({})
  const [groups, setGroups] = useState<ItemGroup[]>([])
  const [items, setItems] = useState<Image[]>([])

  const fetchMenu = async () => {
    try {
      const response = await fetch(`${import.meta.env.VITE_APP_PATH}/menu`);
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      const groupList = await response.json();
      for (const data of groupList) {
        setNavItems((prev) => ({
          ...prev,
          [data.superGroup]: [...(prev[data.superGroup] || []), data.groupName]
        }))
        console.log('data', images)
        setGroups(prev => [...prev, data])
      }
    } catch (error) {
      console.error('Failed to fetch menu:', error);
    }
  };

  useEffect(() => {
    startStream()
    fetchMenu()
  }, []);

  useEffect(() => {
    setItems([...images])
  }, [images]);

  return (
    <>
      <Navbar items={navItems}/>
      <div style={{marginTop: '150px'}} className='image-container' onClick={() => console.log(groups)}>
        {groups.map((group: ItemGroup) => (
          <div key={group.id}>
            <div>
              <h1 id={`${group.groupName}`} style={{scrollMarginTop: '50px'}}>
                {group.groupName}
              </h1>
            </div>
            {items.map((image: Image) => (
              image.itemGroup.groupName === group.groupName &&
              <div style={{marginBottom: '50px', textAlign: 'center'}}>
                <p>Index: {image.index}</p>
                <img src={image.image} alt={`Image ${image.index}`}
                     style={{width: '100vw', height: 'auto', marginTop: '10%'}}/>
              </div>
            ))}
          </div>
        ))}
      </div>
    </>
  )
}

export default AppTest