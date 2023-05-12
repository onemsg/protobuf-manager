import { Toolbar } from "@mui/material";
import React from "react";
import BackTitle from "./BackTitle";


function MainPart({backPath, title, children}) {

  return (
    <main className={ "main-part " + (backPath ? "has-backpath" : "")}>
      <Toolbar variant="dense" />
      {
        backPath && <BackTitle path={backPath} title={title} />
      }
      {children }
    </main>
  );
}

export default MainPart;